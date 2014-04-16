(ns masques.service.request-map-utils)

(defn data
  "returns the data from the given request map."
  [request-map]
  (:data request-map))

(defn action
  "Returns the action from the given request map."
  [request-map]
  (:action request-map))