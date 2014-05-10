(ns masques.service.calls.friend
  (:require [clojure.tools.logging :as logging]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.service.actions.friend :as friend-action]
            [masques.service.core :as service-core]))

(defn friend
  "Sends off an unfriend for the given friend request share."
  [friend-request-share]
  (when friend-request-share
    (let [other-profile (share-model/other-profile friend-request-share)]
      (service-core/send-message
        (profile-model/destination other-profile)
        friend-action/action
        { :data { :profile (profile-model/clean-user-data) } }))))

(defn send-friend
  "Sends a unfriend request for the given friend request."
  [request]
  (when-let [friend-request-share (friend-request-model/send-accept request)]
    (friend friend-request-share)
    friend-request-share))