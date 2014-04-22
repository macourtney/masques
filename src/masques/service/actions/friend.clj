(ns masques.service.actions.friend
  (:refer-clojure :exclude [name])
  (:require [clojure.tools.logging :as logging]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share]
            [masques.service.request-map-utils :as request-map-utils]))

(def action "friend")

(defn data
  "Returns the data map from the request-map."
  [request-map]
  (:data request-map))

(defn read-friend-profile
  "Returns the friend profile which comes from the person requesting the
unfriend."
  [request-map]
  (let [profile (:profile (data request-map))]
    (profile-model/find-profile
      (select-keys profile
        [profile-model/identity-key profile-model/identity-algorithm-key]))))

(defn accept
  "Updates the friend request as accepted from the given request-map and saves
it to the database."
  [request-map]
  (let [friend-profile (read-friend-profile request-map)
        friend-request (friend-request-model/find-by-profile friend-profile)]
    (friend-request-model/accept friend-request))
  true)

(defn run
  "Updates the profile in the given request map as rejected. If successful,
returns true. Otherwise, throws an exception."
  [request-map]
  { :data
   { :received? (accept request-map) }})