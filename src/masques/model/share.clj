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
(def from-profile-key :from-profile)

(defn find-share
  "Finds the share with the given prototype. If the given record is an int, then
this function finds the share by id."
  [record]
  (if (integer? record)
    (find-by-id share record)
    (find-first share record)))

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

(defn from-profile
  "Returns the from profile attached to the given share."
  [share]
  (or (from-profile-key share)
      (profile-model/find-profile (profile-from-id-key share))))

(defn attach-from-profile
  "Sets the profile from id to the profile attached to the given share with the
key from-profile-key. If no from profile is set on the given share, then this
function simply returns the share.."
  [share]
  (if-let [from-profile (from-profile-key share)]
    (let [share (dissoc share from-profile-key)]
      (assoc share profile-from-id-key (id from-profile)))
    share))

(defn from-profile-is-current-user?
  "If the from profile of the given share is the current user then the from
profile is returned."
  [share]
  (let [share-from-profile (from-profile share)]
    (when (profile-model/current-user? share-from-profile)
      share-from-profile)))

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

(defn to-profile-is-current-user?
  "If the to profile of the given share is the current user then the to profile
is returned."
  [share]
  (let [share-to-profile (to-profile share)]
    (when (profile-model/current-user? share-to-profile)
      share-to-profile)))

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

(defn other-profile
  "Returns the to or from profile which is not the current user."
  [share-record]
  (profile-model/find-profile
    (or (profile-model/not-current-user? (profile-to-id-key share-record))
        (profile-model/not-current-user? (profile-from-id-key share-record)))))

(defn delete-share [share-record]
  (when share-record
    (message-model/delete-message (message-id share-record))
    (delete-content share-record)
    (delete-record share share-record)))

(defn create-share
  "Creates a share which is sent out to a peer."
  [share]
  (save (attach-to-identity (attach-from-profile (attach-message-id share)))))

(defn find-friend-request-share-with-to-profile
  "Finds the friend request share sent to the given profile."
  [profile]
  (find-first share
    { content-type-key friend-content-type
      profile-to-id-key (id profile) }))

(defn find-friend-request-share-with-from-profile
  "Finds the friend request share sent from the given profile."
  [profile]
  (find-first share
    { content-type-key friend-content-type
      profile-from-id-key (id profile) }))

(defn update-message
  "Updates the given share to have the given message."
  [share message]
  (message-model/update-message (message-id share) message)
  share)

(defn create-send-friend-request-share
  "Creates a friend request share."
  [message profile friend-request]
  (if-let [old-share (find-friend-request-share-with-to-profile profile)]
    (update-message old-share message)
    (create-share
      { content-type-key friend-content-type
        message-key message
        profile-to-id-key (id profile)
        profile-from-id-key (id (profile-model/current-user))
        content-id-key (id friend-request) })))

(defn create-received-friend-request-share
  "Creates an incoming friend request share."
  [message profile friend-request]
  (if-let [old-share (find-friend-request-share-with-from-profile profile)]
    (update-message old-share message)
    (create-share { content-type-key friend-content-type
                    message-key message
                    profile-to-id-key (id (profile-model/current-user))
                    profile-from-id-key (id profile)
                    content-id-key (id friend-request) })))

(defn find-friend-request-share
  "Finds the share for the given friend request. Friend request can be either an
integer id or map containing the id of the friend request."
  [friend-request & share-fields]
  (when friend-request
    (let [friend-request-id (id friend-request)]
      (first
        (select share
                ;(apply fields share-fields) Doesn't work since select is a macro
                (where { (h2-keyword content-type-key) friend-content-type
                         (h2-keyword content-id-key) friend-request-id })
                (limit 1))))))