(ns masques.service.calls.send-status
  (:require [clj-i2p.core :as clj-i2p]
            [clojure.tools.logging :as logging]
            [masques.model.base :as base-model]
            [masques.model.grouping :as grouping-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.model.share-profile :as share-profile-model]
            [masques.service.actions.update-status :as update-status-action]
            [masques.service.core :as service-core]))

(defn data
  "Returns the data map from the request-map."
  [response-map]
  (:data response-map))

(defn send-status-to-profile
  "Sends off a status update to the given profile. If no profile is given, then
the to profile on the given status share is used."
  [status-share profile]
    (when (and status-share profile)
      (when-let [response (service-core/send-message
                            (profile-model/destination profile)
                            update-status-action/action
                            { clj-i2p/data-key
                             { :message (share-model/message-text status-share)
                               :uuid (base-model/uuid status-share)
                               :profile
                                 (select-keys
                                   (profile-model/current-user)
                                   [profile-model/identity-key
                                    profile-model/identity-algorithm-key]) } })]
        (when (:received? (data response))
            (share-profile-model/update-transferred-at-to-now
              status-share profile)))))

(defn send-status
  "Sends a status update."
  [message groups profiles]
  (when-let [status-share (share-model/find-share
                            (share-model/create-status-share
                              message groups profiles))]
    (doseq [profile (share-model/all-to-profiles status-share)]
      (send-status-to-profile status-share profile))
    status-share))

(defn send-to-default-group
  "Sends a status update to the default group."
  [message]
  (send-status message [(grouping-model/find-friends-id)] nil))