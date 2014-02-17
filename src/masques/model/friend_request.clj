(ns masques.model.friend-request
  (:require [clj-time.core :as clj-time]
            [masques.model.profile :as profile])
  (:use masques.model.base))

(def request-status-key :request-status)
(def requested-at-key :requested-at)
(def request-approved-at-key :request-approved-at)
(def profile-id-key :profile-id)

(def pending-status "pending")

(defn delete-friend-request
  "Deletes the given friend request from the database. The friend request should
include the id."
  [friend-request-record]
  (profile/delete-profile { :id (profile-id-key friend-request-record) })
  (delete-record friend-request friend-request-record))

(defn set-requested-at [record]
  (if (or (requested-at-key record) (:REQUESTED_AT record))
    record 
    (conj record {:REQUESTED_AT (str (clj-time/now))})))

(defn save [record]
  (let [clean (set-requested-at record)]
    (insert-or-update friend-request clean)))

(defn send-request
  "Creates a new friend request and attaches a new profile and new share to it."
  [masques-id-file]
 ; We need to create a share, attach a friend request and profile to it.
  (when-let [new-profile (profile/load-masques-id-file masques-id-file)]
    (save
      { request-status-key pending-status
        profile-id-key (:id new-profile) })))