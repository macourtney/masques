(ns masques.service.actions.unfriend
  (:refer-clojure :exclude [name])
  (:require [masques.model.friend-request :as friend-request-model]
            [masques.model.share :as share]
            [masques.service.request-map-utils :as request-map-utils]))

(def action "unfriend")

(def received-key :received?)

(defn data
  "Returns the data map from the request-map."
  [request-map]
  (:data request-map))

(defn read-friend-profile
  "Returns the friend profile which comes from the person requesting the
unfriend."
  [request-map]
  (:profile (data request-map)))

(defn unfriend
  "Updates the friend request as unfriend from the given request-map and saves
it to the database."
  [request-map]
  (let [friend-profile (read-friend-profile request-map)]
    (friend-request-model/unfriend friend-profile))
  true)

(defn run
  "Creates an unfriend request. If successful, returns true. Otherwise, throws
an exception."
  [request-map]
  { :data
   { :received? (unfriend request-map) }})