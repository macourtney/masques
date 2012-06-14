(ns masques.model.friend
  (:require [clj-record.boot :as clj-record-boot]
            [masques.model.identity :as identity])
  (:use masques.model.base))

(clj-record.core/init-model
  (:associations (belongs-to identity)
                 (belongs-to friend :fk friend_id :model identity)))

(defn all-friends
  "Returns all of the friends for the current identity"
  ([] (all-friends (identity/current-user-identity)))
  ([identity]
    (find-records { :identity_id (:id identity) })))

(defn add-friend
  "Removes the given friend for the given or current identity."
  ([friend-identity] (add-friend friend-identity (identity/current-user-identity)))
  ([friend-identity identity]
    (when-let [identity-id (:id identity)]
      (when-let [friend-id (:id friend-identity)]
        (insert { :identity_id identity-id :friend_id friend-id })))))

(defn friend?
  "Returns true if the given friend is a friend of the given or current identity."
  ([friend-identity] (friend? friend-identity (identity/current-user-identity)))
  ([friend-identity identity]
    (when-let [identity-id (:id identity)]
      (when-let [friend-id (:id friend-identity)]
        (find-record { :identity_id identity-id :friend_id friend-id })))))

(defn remove-friend
  "Removes the given friend for the given or current identity."
  ([friend-identity] (remove-friend friend-identity (identity/current-user-identity)))
  ([friend-identity identity]
    (when-let [friend-to-remove (friend? friend-identity identity)]
      (destroy-record friend-to-remove))))

