(ns masques.model.share
  (:require [masques.model.message :as message-model]
            [masques.model.profile :as profile-model]
            [clojure.tools.logging :as logging])
  (:use masques.model.base
        korma.core)
  (:import [java.util Date]))

(def friend-content-type "FRIEND_REQUEST")

(def content-id-key :content-id)
(def content-type-key :content-type)
(def profile-from-id-key :profile-from-id)
(def profile-to-id-key :profile-to-id)
(def message-id-key :message-id)

(def content-key :content)
(def message-key :message)
(def to-profile-key :to-profile)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Message.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn needs-message [share-record]
  (and (message-id-key share-record) (not (message-key share-record))))

(defn assoc-message [share-record]
  (assoc share-record message-key
         (message-model/find-message (message-id-key share-record))))

(defn attach-message [share-record]
  (if (needs-message share-record) (assoc-message share-record) share-record))

(defn attach-message-id
  "Attaches the message to the given share record. Creates the message record if
it needs to be created."
  [share]
  (if-let [message (message-key share)]
    (let [share (dissoc share message-key)]
      (assoc share message-id-key (id (message-model/find-or-create message))))
    share))

(defn message-id
  "Returns the message id for the message attached to the given share."
  [share]
  (message-id-key share))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Content.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-type [share-record]
  (upper-case (content-type-key share-record)))

(defn needs-content [share-record]
  (and (content-id-key share-record) (not (content-key share-record))))

(defn get-content [share-record]
  (find-by-id (get-type share-record) (content-id-key share-record)))

(defn delete-content [share-record]
  (delete-record (get-type share-record) (content-id-key share-record)))

(defn assoc-content [share-record]
  (assoc share-record content-key (get-content share-record)))

(defn attach-content [share-record]
  (if (needs-content share-record)
    (assoc-content share-record)
    share-record))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; From identity
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn attach-from-profile
  "Attaches the from identity using the logged in user's identity. If
:profile-from-id is set, then this function simply returns the record."
  [share]
  (if (profile-from-id-key share)
    (let [current-user-profile (profile-model/current-user)]
      (assoc share profile-from-id-key (id current-user-profile)))
    share))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; To identity
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn to-profile
  "Returns the to profile attached to the given share."
  [share]
  (or (to-profile-key share)
      (profile-model/find-profile (profile-to-id-key share))))

(defn remove-to-profile
  "Removes the to-profile key from the given share."
  [share]
  (dissoc share to-profile-key))

(defn attach-to-identity
  "Attaches the to identity to the given share record."
  [share]
  (let [sanitized-share (remove-to-profile share)]
    (if-let [to-profile (to-profile share)]
      (assoc sanitized-share profile-to-id-key (id to-profile))
      sanitized-share)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Build/Save.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn build [share-record]
  (attach-message (attach-content share-record)))

(defn get-and-build [share-id]
  (build (find-by-id share share-id)))

(defn save-content [share-record]
  (insert-or-update (get-type share-record) (content-key share-record)))

(defn save [record]
  (insert-or-update share record))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Sending and receiving of shares.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn is-type
  "Returns true if the given share is of the given type."
  [share-record content-type]
  (= (get-type share-record) content-type))

(defn is-friend-request [share-record]
  (is-type share-record friend-content-type))

(defn is-from-friend [share-record]
  (println (str "\nTesting if from from friend " share-record))
  true)

(defn delete-share [share-record]
  (when share-record
    (message-model/delete-message (message-id share-record))
    (delete-content share-record)
    (delete-record share share-record)))

(defn create-share
  "Creates a share which is sent out to a peer."
  [share]
  (save (attach-to-identity (attach-from-profile (attach-message-id share)))))

(defn find-friend-request-share-with-profile
  "Finds the friend request share pointing to the given profile."
  [profile]
  (find-first share
    { content-type-key friend-content-type
      profile-to-id-key (id profile) }))

(defn create-friend-request-share
  "Creates a friend request share."
  [message profile friend-request]
  (if-let [old-share (find-friend-request-share-with-profile profile)]
    (do
      (message-model/update-message (message-id old-share) message)
      old-share)
    (create-share
      { content-type-key friend-content-type
        message-key message
        to-profile-key profile
        content-id-key (id friend-request) })))

(defn create-received-friend-request-share
  "Creates an incoming friend request share."
  [message friend-request]
  (create-share
    { content-type-key friend-content-type
      message-key message
      to-profile-key (id (profile-model/current-user))
      content-id-key (id friend-request) }))

(defn find-friend-request-share
  "Finds the share for the given friend request. Friend request can be either an
integer id or map containing the id of the friend request."
  [friend-request & share-fields]
  (when friend-request
    (let [friend-request-id (if (map? friend-request)
                              (:id friend-request)
                              friend-request)
          share-fields (if (empty? share-fields) [:id] share-fields)]
      (first
        (select share
                ;(apply fields share-fields) Doesn't work since select is a macro
                (where { (h2-keyword content-type-key) friend-content-type
                         (h2-keyword content-id-key) friend-request-id })
                (limit 1))))))