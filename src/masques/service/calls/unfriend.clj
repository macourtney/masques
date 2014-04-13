(ns masques.service.calls.unfriend
  (:require [clojure.tools.logging :as logging]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.message :as message-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.service.actions.unfriend :as unfriend-action]
            [masques.service.core :as service-core]))

(defn profile
  "Returns a sanitized profile to be passed in the unfriend request."
  []
  (select-keys (profile-model/current-user)
    [profile-model/alias-key profile-model/identity-key
     profile-model/identity-algorithm-key]))

(defn unfriend
  "Sends off an unfriend for the given friend request."
  [friend-request]
  (when friend-request
    (let [to-profile (friend-request-model/find-to-profile friend-request)]
      (service-core/send-message
        (profile-model/destination to-profile)
        unfriend-action/action
        { :profile (profile) }))))

(defn send-unfriend
  "Sends a unfriend request for the given friend request."
  [request]
  (when-let [friend-request (friend-request-model/unfriend request)]
    (unfriend friend-request)
    friend-request))