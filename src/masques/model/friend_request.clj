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
(def rejected-status "rejected")
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
  (share/delete-share (share/find-friend-request-share friend-request-record))
  (delete-record friend-request friend-request-record))

(defn status
  "Returns the status of the given request. If a new status is given, then this
function updates the status for the given request."
  ([request]
    (request-status-key request))
  ([request new-status]
    (let [approved-at (when (= new-status approved-status)
                        (str (clj-time/now)))]
      (update-record friend-request
        { id-key (id request)
          request-status-key new-status
          request-approved-at-key approved-at }))
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

(defn update-send-request
  "Updates the given request to a send-request. Returns the updated or new share
unless it is already approved.."
  [message new-profile request]
  (let [request-status (status request)]
    (condp = request-status
      approved-status
        (share/find-friend-request-share-with-to-profile new-profile)
      pending-received-status
        (do
          (status request approved-status)
          (share/create-send-friend-request-share message new-profile request))
      pending-sent-status
        (share/create-send-friend-request-share message new-profile request)
      rejected-status nil
      unfriend-status 
        (do
          (status request pending-sent-status)
          (share/create-send-friend-request-share message new-profile request))
      (throw (RuntimeException. (str "Unknown status: " request-status))))))

(defn create-new-send-request
  "Creates a new send request from the given message and profile."
  [message new-profile]
  (let [new-request (save
                      { request-status-key pending-sent-status
                        profile-id-key (id new-profile) })]
    (share/create-send-friend-request-share message new-profile new-request)))

(defn send-request
  "Creates a new friend request and attaches a new profile and new share to it."
  [masques-id-file message]
 ; We need to create a share, attach a friend request and profile to it.
  (when-let [new-profile (profile/load-masques-id-file masques-id-file)]
    (if-let [old-request (find-by-profile new-profile)]
      (update-send-request message new-profile old-request)
      (create-new-send-request message new-profile))))

(defn update-receive-request
  "Updates the given request to a send-request. Returns the updated or new share
unless it is already approved.."
  [message new-profile request]
  (let [request-status (status request)]
    (condp = request-status
      approved-status
        (share/find-friend-request-share-with-from-profile new-profile) 
      pending-received-status
        (share/create-received-friend-request-share message new-profile request)
      pending-sent-status
        (do
          (status request approved-status)
          (share/create-received-friend-request-share message new-profile
                                                      request))
      rejected-status
        (do
          (status request approved-status)
          (share/create-received-friend-request-share message new-profile
                                                      request))
      unfriend-status nil ; Already unfriended.
      (throw (RuntimeException. (str "Unknown status: " request-status))))))

(defn create-new-receive-request
  "Creates a new send request from the given message and profile."
  [message new-profile]
  (let [new-request (save
                        { request-status-key pending-received-status
                          profile-id-key (id new-profile)
                          requested-at-key (str (clj-time/now)) })]
      (share/create-received-friend-request-share message new-profile
                                                  new-request)))

(defn receive-request
  "Creates a new incoming friend request"
  [profile message]
  (when-let [new-profile (profile/load-masques-id-map profile)]
    (if-let [old-request (find-by-profile new-profile)]
      (update-receive-request message new-profile old-request)
      (create-new-receive-request message new-profile))))

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

(defn count-friends
  "Returns the number of accepted friend requests."
  []
  (count-requests
    { (h2-keyword request-status-key) approved-status }))

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

(defn approved-received-request
  "Returns the approved friend request at the given index."
  [index]
  (find-table-request index
    { (h2-keyword request-status-key) approved-status }))

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

(defn update-to-unfriend
  "Updates the given request status to unfriend and returns the share attached
to the given request."
  [request]
  (do
    (status request unfriend-status)
    (share/find-friend-request-share request)))

(defn unfriend
  "Updates a request as unfriended."
  [request]
  (when-let [request-id (id request)]
    (when-let [request (find-friend-request request-id)]
      (let [request-status (status request)]
        (condp = request-status
          approved-status
            (update-to-unfriend request)
          pending-received-status
            (update-to-unfriend request)
          pending-sent-status
            (delete-friend-request request)
          rejected-status nil
          unfriend-status nil ; Already unfriended.
          (throw (RuntimeException. (str "Unknown status: " request-status))))))))

(defn update-to-rejected
  "Updates the given request status to unfriend and returns the share attached
to the given request."
  [request]
  (do
    (status request rejected-status)
    (share/find-friend-request-share request)))

(defn reject
  "Updates a request as rejected."
  [request]
  (when-let [request-id (id request)]
    (when-let [request (find-friend-request request-id)]
      (let [request-status (status request)]
        (condp = request-status
          approved-status
            (update-to-rejected request)
          pending-received-status
            (delete-friend-request request)
          pending-sent-status
            (update-to-rejected request)
          rejected-status nil ; Already rejected..
          unfriend-status nil 
          (throw (RuntimeException.
                   (str "Unknown status: " request-status))))))))

(defn update-to-accepted
  "Updates the given request status to accepted and returns the share attached
to the given request."
  [request]
  (do
    (status request approved-status)
    (share/find-friend-request-share request)))

(defn send-accept
  "Updates a request as accepted when send from the currently logged in user."
  [request]
  (when-let [request-id (id request)]
    (when-let [request (find-friend-request request-id)]
      (let [request-status (status request)]
        (condp = request-status
          approved-status nil
          pending-received-status
            (update-to-accepted request)
          pending-sent-status nil
          rejected-status nil ; Already rejected..
          unfriend-status
            (update-to-accepted request) 
          (throw (RuntimeException.
                   (str "Unknown status: " request-status))))))))

(defn receive-accept
  "Updates a request as accepted when received from someone who you sent a
friend request."
  [request]
  (when-let [request-id (id request)]
    (when-let [request (find-friend-request request-id)]
      (let [request-status (status request)]
        (condp = request-status
          approved-status nil
          pending-received-status nil
          pending-sent-status
            (update-to-accepted request)
          rejected-status (update-to-accepted request) ; Already rejected..
          unfriend-status nil
          (throw (RuntimeException.
                   (str "Unknown status: " request-status))))))))