(ns masques.service.request-map-utils)

(defn data
  "returns the data from the given request map."
  [request-map]
  (:data request-map))

(defn action
  "Returns the action from the given request map."
  [request-map]
  (:action request-map))

(defn from
  "Returns the from map from the given request map."
  [request-map]
  (:from request-map))

(defn from-destination
  "Returns the destination the request map was sent from."
  [request-map]
  (:destination (from request-map)))