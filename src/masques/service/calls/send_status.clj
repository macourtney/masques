(ns masques.service.calls.send-status
  (:require [clojure.tools.logging :as logging]
            [masques.model.grouping :as grouping-model]
            [masques.model.share :as share-model]
            [masques.service.actions.update-status :as update-status-action]
            [masques.service.core :as service-core]))

(defn data
  "Returns the data map from the request-map."
  [response-map]
  (:data response-map))

(defn send-status-to-profile
  "Sends off a status update to the given profile. If no profile is given, then
the to profile on the given status share is used."
  ([status-share]
    (send-status-to-profile status-share (to-profile status-share)))
  ([status-share profile]
    (when (and status-share profile)
      (when-let [response (service-core/send-message
                            (profile-model/destination profile)
                            update-status-action/action
                            { :data
                             { :message (share-model/message-text status-share)
                               :uuid (share-model/uuid status-share) } })]
        (when (:received? (data response))
          )))))

(defn send-status
  "Sends a status update."
  [message group profile]
  (when-let [status-share (share-model/create-status-share message group profile)]
    (send-status-to-profile status-share)
    (doseq [group-profile (grouping-model/get-profiles
                            (share-model/to-group status-share))]
      (send-status-to-profile status-share group-profile))
    status-share))