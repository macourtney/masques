(ns masques.model.share
  (:require [clj-time.core :as clj-time]
            [clojure.tools.logging :as logging]
            [masques.model.message :as message-model]
            [masques.model.profile :as profile-model]
            [masques.model.grouping :as grouping-model]
            [masques.model.share-profile :as share-profile-model])
  (:use masques.model.base
        korma.core))

(def friend-content-type "FRIEND_REQUEST")
(def status-type "STATUS")

(def content-id-key :content-id)
(def content-type-key :content-type)
(def message-id-key :message-id)
(def profile-from-id-key :profile-from-id)
(def uuid-key :uuid)

(def content-key :content)
(def message-key :message)
(def to-profile-key :to-profile)
(def from-profile-key :from-profile)
(def group-key :group)

(defn add-share-delete-interceptor
  "Adds the given share delete interceptor."
  [interceptor]
  (add-delete-interceptor share interceptor))

(defn remove-share-delete-interceptor
  "Removes the given share delete interceptor."
  [interceptor]
  (remove-delete-interceptor share interceptor))

(defn add-share-insert-interceptor
  "Adds the given share insert interceptor."
  [interceptor]
  (add-insert-interceptor share interceptor))

(defn remove-share-insert-interceptor
  "Removes the given share insert interceptor."
  [interceptor]
  (remove-insert-interceptor share interceptor))

(defn add-share-update-interceptor
  "Adds the given share update interceptor."
  [interceptor]
  (add-update-interceptor share interceptor))

(defn remove-share-update-interceptor
  "Removes the given share update interceptor."
  [interceptor]
  (remove-update-interceptor share interceptor))

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

(defn message-text
  "Returns the text of the message attached to the given share."
  [share]
  (or (message-key share) (message-model/body (message-id share))))

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

(defn first-other-profile
  "Returns the first to or from profile which is not the current user."
  [share-record]
  (let [share-record (if (integer? share-record)
                       (find-share share-record)
                       share-record)]
    (profile-model/find-profile
      (or (profile-model/not-current-user?
            (profile-from-id-key share-record))
          (profile-model/not-current-user?
            (share-profile-model/first-profile-id-for-share share-record))))))

(defn delete-share [share-record]
  (when share-record
    (let [share-record (find-share (id share-record))]
      (share-profile-model/delete-all share-record)
      (message-model/delete-message (message-id share-record))
      (delete-content share-record)
      (delete-record share share-record))))

(defn create-share
  "Creates a share which is sent out to a peer."
  [share]
  (save (attach-from-profile (attach-message-id share))))

(defn find-friend-request-share-with-to-profile
  "Finds the friend request share sent to the given profile."
  [profile]
  (some identity
        (map
          #(find-first share { content-type-key friend-content-type id-key % })
          (share-profile-model/share-ids-for-profile profile))))

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
  (id share))

(defn create-send-friend-request-share
  "Creates a friend request share."
  [message profile friend-request]
  (if-let [old-share (find-friend-request-share-with-to-profile profile)]
    (update-message old-share message)
    (let [share-id (create-share
                     { content-type-key friend-content-type
                       content-id-key (id friend-request)
                       message-key message
                       profile-from-id-key (id (profile-model/current-user)) })]
      (share-profile-model/create-share-profile share-id (id profile))
      share-id)))

(defn create-received-friend-request-share
  "Creates an incoming friend request share."
  [message profile friend-request]
  (if-let [old-share (find-friend-request-share-with-from-profile profile)]
    (update-message old-share message)
    (let [share-id (create-share { content-type-key friend-content-type
                                   content-id-key (id friend-request)
                                   message-key message 
                                   profile-from-id-key (id profile) })]
      (share-profile-model/create-share-profile
        share-id (id (profile-model/current-user)))
      share-id)))

(defn find-friend-request-share
  "Finds the share for the given friend request. Friend request can be either an
integer id or map containing the id of the friend request."
  [friend-request & share-fields]
  (when friend-request
    (let [friend-request-id (id friend-request)]
      (clean-up-for-clojure
        (first
          (select share
                  (where { (h2-keyword content-type-key) friend-content-type
                           (h2-keyword content-id-key) friend-request-id })
                  (limit 1)))))))

(defn create-status-share
  "Creates a new status share with the given message sent to the group id
and/or profile id."
  [message group-ids profile-ids]
  (when-let [share-id (create-share { content-type-key status-type
                                      message-key message
                                      profile-from-id-key
                                        (id (profile-model/current-user)) })]
    (doseq [profile-id profile-ids]
      (share-profile-model/create-share-profile share-id profile-id))
    (doseq [group-id group-ids]
      (share-profile-model/create-all-share-profiles-for-group
        share-id group-id))
    share-id))

(defn create-received-share
  "Creates a new share received from a friend."
  [message from-profile uuid]
  (when-let [share-id (create-share { content-type-key status-type
                                      message-key message
                                      profile-from-id-key (id from-profile)
                                      uuid-key uuid })]
    (share-profile-model/create-share-profile
      share-id (id (profile-model/current-user)) nil (clj-time/now))
    share-id))

(defn all-to-profiles
  "Returns all of the to profiles for the given share."
  [share]
  (when share
    (share-profile-model/profile-ids-for-share share)))

(defn count-stream-shares
  "Counts all of the shares which should show up in the stream."
  []
  (:count
    (first
      (select
        share
        (aggregate (count :*) :count)
        (join share-profile
          (= (h2-keyword share-profile share-profile-model/share-id-key)
             id-key))
        (where
          { 
            (h2-keyword share-profile share-profile-model/profile-to-id-key)
              (id (profile-model/current-user))
            (h2-keyword content-type-key) [not= friend-content-type] })))))

(defn find-stream-share-at
  "Returns a stream share at the given index."
  [index]
  (first
    (select
      share
      (join share-profile
        (= (h2-keyword share-profile share-profile-model/share-id-key) id-key))
      (where
          { (h2-keyword share-profile share-profile-model/profile-to-id-key)
              (id (profile-model/current-user))
            (h2-keyword content-type-key) [not= friend-content-type] })
      (limit 1)
      (offset index))))

(defn index-of-stream-share
  "Returns the index of the given share or id in the set of shares which should
be shown in the stream."
  [share-id]
  (index-of
    (id share-id)
    (select
      share
      (fields id-key)
      (join share-profile
        (= (h2-keyword share-profile share-profile-model/share-id-key) id-key))
      (where
        { (h2-keyword share-profile share-profile-model/profile-to-id-key)
            (id (profile-model/current-user))
          (h2-keyword content-type-key) [not= friend-content-type] }))))