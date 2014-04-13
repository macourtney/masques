(ns masques.model.friend-request
  (:require [clj-time.core :as clj-time]
            [clojure.tools.logging :as logging]
            [korma.core :as korma]
            [masques.model.profile :as profile]
            [masques.model.share :as share])
  (:use masques.model.base))

(def created-at-key :created-at)
(def request-status-key :request-status)
(def requested-at-key :requested-at)
(def request-approved-at-key :request-approved-at)
(def profile-id-key :profile-id)

(def approved-status "approved")
(def pending-received-status "pending-received")
(def pending-sent-status "pending-sent")
(def unfriend-status "unfriend")

(defn find-friend-request
  "Returns the friend request with the given id."
  [request-id]
  (when request-id
    (find-by-id friend-request (id request-id))))

(defn delete-friend-request
  "Deletes the given friend request from the database. The friend request should
include the id."
  [friend-request-record]
  (profile/delete-profile { :id (profile-id-key friend-request-record) })
  (delete-record friend-request friend-request-record))

(defn status
  "Returns the status of the given request. If a new status is given, then this
function updates the status for the given request."
  ([request]
    (request-status-key request))
  ([request new-status]
    (update-record friend-request
      { id-key (id request) request-status-key new-status })
    new-status))

(defn set-requested-at [record]
  (if (or (requested-at-key record) ((h2-keyword requested-at-key) record))
    record 
    (conj record { (h2-keyword requested-at-key) (str (clj-time/now)) })))

(defn update-requested-at
  "Updateds the given record with requested-at to now."
  [record]
  (update-record friend-request
    { :ID (id record)
      (h2-keyword requested-at-key) (str (clj-time/now)) }))

(defn requested-at-set?
  "Returns true if the given request is set."
  [request]
  (let [request (if (integer? request) (find-friend-request request) request)]
    (requested-at-key request)))

(defn requested-at
  "Returns when the friend request was received by the target computer and
user."
  [record]
  (requested-at-key record))

(defn save
  "Saves the given friend request to the database."
  [record]
  (insert-or-update friend-request record))

(defn find-by-profile
  "Finds the friend request with the given profile."
  [profile]
  (find-first friend-request { profile-id-key (id profile) }))

(defn update-to-send-request
  "Updates the given request to a send-request. Returns the updated or new share
unless it is already approved.."
  [message new-profile request]
  (let [request-status (status request)]
    (condp = request-status
      approved-status nil
      pending-received-status nil ; This should approve the request.
      pending-sent-status
        (share/create-friend-request-share message new-profile request)
      unfriend-status 
        (do
          (status request pending-sent-status)
          (share/create-friend-request-share message new-profile request))
      (throw (RuntimeException. (str "Unknown status: " request-status))))))

(defn create-new-send-request
  "Creates a new send request from the given message and profile."
  [message new-profile]
  (let [new-request (save
                      { request-status-key pending-sent-status
                        profile-id-key (id new-profile) })]
    (share/create-friend-request-share message new-profile new-request)))

(defn send-request
  "Creates a new friend request and attaches a new profile and new share to it."
  [masques-id-file message]
 ; We need to create a share, attach a friend request and profile to it.
  (when-let [new-profile (profile/load-masques-id-file masques-id-file)]
    (if-let [old-request (find-by-profile new-profile)]
      (update-to-send-request message new-profile old-request)
      (create-new-send-request message new-profile))))

(defn receive-request
  "Creates a new incoming friend request"
  [profile message]
  (when-let [new-profile (profile/save profile)]
    (let [new-request (save
                        { request-status-key pending-received-status
                          profile-id-key (id new-profile)
                          requested-at-key (str (clj-time/now)) })]
      (share/create-received-friend-request-share message new-request))))

(defn count-requests
  "Counts all of the requests which satisfies the given korma where-map."
  [where-map]
  (:count
    (first
      (korma/select
        friend-request
        (korma/aggregate (count :*) :count)
        (korma/where where-map)))))

(defn count-pending-sent-requests
  "Returns the number of requests pending."
  []
  (count-requests { (h2-keyword request-status-key) pending-sent-status }))

(defn count-pending-received-requests
  "Returns the number of requests waiting to be accepted by the user."
  []
  (count-requests
    { (h2-keyword request-status-key) pending-received-status }))

(defn find-table-request
  "Returns a friend request with just the id and profile id at the given index
with the given where map."
  [index where-map]
  (first
    (korma/select
      friend-request
      (korma/fields (h2-keyword :id) (h2-keyword profile-id-key))
      (korma/where where-map)
      (korma/limit 1)
      (korma/offset index))))

(defn pending-sent?
  "Returns true if the given request has a status of pending sent."
  [request]
  (let [request (if (integer? request) (find-friend-request request) request)]
    (= (request-status-key request) pending-sent-status)))

(defn pending-sent-request
  "Returns the pending request at the given index."
  [index]
  (find-table-request index
    { (h2-keyword request-status-key) pending-sent-status }))

(defn pending-received-request
  "Returns the pending received request at the given index."
  [index]
  (find-table-request index
    { (h2-keyword request-status-key) pending-received-status }))

(defn find-to-profile
  "Returns the profile for the given request"
  [request]
  (let [request-id (id request)]
    (profile/find-profile
      (profile-id-key
        (clean-up-for-clojure
          (first
            (korma/select friend-request
              (korma/fields (h2-keyword profile-id-key))
              (korma/where { :ID request-id }))))))))

(defn unfriend
  "Updates the given request to an unfriend."
  [request]
  (when-let [request-id (id request)]
    (when-let [request (find-friend-request request-id)]
      (update-record friend-request
             { :ID (id request)
               (h2-keyword request-status-key) unfriend-status
               (h2-keyword requested-at-key) nil
               (h2-keyword request-approved-at-key) nil })
      (when (not
              (and
                (pending-sent? request)
                (requested-at-set? request)))
         (find-friend-request request)))))