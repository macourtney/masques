(ns masques.service.calls.request-friendship
  (:require [clojure.tools.logging :as logging]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.message :as message-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.service.actions.request-friendship
              :as request-friendship-action]
            [masques.service.core :as service-core]))

(defn find-to-profile
  "Finds the to-profile attached to the given share."
  [share]
  (share-model/to-profile share))

(defn message
  "Returns the message body of the message attached to the given friend request
share."
  [share]
  (message-model/body (share-model/message-id share)))

(defn profile
  "Returns a sanitized profile to be passed in the friendship request."
  []
  (select-keys (profile-model/current-user)
    [profile-model/alias-key profile-model/identity-key
     profile-model/identity-algorithm-key]))

(defn create-data-map
  "Creates the data map for a request-friendship action from the given friend
request share."
  [share]
  { :message (message share)
    :profile (profile) })

(defn received?
  "Returns true if the friend request was received."
  [response-map]
  (request-friendship-action/received-key (:data response-map)))

(defn update-friend-request
  "Updates the friend request for the given share with the received time."
  [share]
  (friend-request-model/update-requested-at (share-model/get-content share)))

(defn request-friendship
  "Sends off a friend request for the given friend request share."
  [share]
  (let [to-profile (find-to-profile share)]
    (when (received?
            (service-core/send-message
              (profile-model/destination to-profile)
              request-friendship-action/action
              (create-data-map share)))
      (update-friend-request share))))

(defn send-friend-request
  "Sends a friend request given the mid file and the message text."
  [mid-file message-text]
  (let [share (friend-request-model/send-request mid-file message-text)]
    (request-friendship share)
    share))