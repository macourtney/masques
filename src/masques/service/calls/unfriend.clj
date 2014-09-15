(ns masques.service.calls.unfriend
  (:require [clojure.tools.logging :as logging]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.service.actions.unfriend :as unfriend-action]
            [masques.service.core :as service-core]))

(defn unfriend
  "Sends off an unfriend for the given friend request share."
  [friend-request-share]
  (when friend-request-share
    (when-let [other-profile (share-model/first-other-profile friend-request-share)]
      (service-core/send-message
        (profile-model/destination other-profile)
        unfriend-action/action
        { :data { :profile (profile-model/clean-user-data) } }))))

(defn send-unfriend
  "Sends an unfriend request for the given friend request."
  [request]
  (when-let [friend-request-share (share-model/find-friend-request-share request)]
    (friend-request-model/unfriend request)
    (unfriend friend-request-share)))