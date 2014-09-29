(ns masques.service.actions.update-status
  (:refer-clojure :exclude [name])
  (:require [clojure.tools.logging :as logging]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.service.request-map-utils :as request-map-utils]))

(def action "update-status")

(defn data
  "Returns the data map from the request-map."
  [request-map]
  (:data request-map))

(defn read-profile
  "Returns the profile which the status came from."
  [request-map]
  (let [profile (:profile (data request-map))]
    (profile-model/find-profile
      (select-keys profile
        [profile-model/identity-key profile-model/identity-algorithm-key]))))

(defn read-message
  "Returns the message of the status update."
  [request-map]
  (:message (data request-map)))

(defn read-uuid
  "Returns the uuid of the status update share."
  [request-map]
  (:uuid (data request-map)))

(defn accept
  "Parses the given request map for a status update, saves the status, and
returns the uuid."
  [request-map]
  (let [from-profile (read-profile request-map)
        message (read-message request-map)
        uuid (read-uuid request-map)]
    (logging/debug "Received from" (profile-model/alias from-profile)
                   "status update:" message)
    (when (share-model/create-received-share message from-profile uuid)
      true)))

(defn run
  "Updates the profile in the given request map as accepted. If successful,
returns true. Otherwise, throws an exception."
  [request-map]
  { :data
   { :received? (accept request-map) }})