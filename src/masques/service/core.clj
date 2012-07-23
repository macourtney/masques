(ns masques.service.core
  (:require [clj-i2p.client :as clj-i2p-client]
            [masques.service.actions.profile :as profile]))

(def service-name :masques-service)

(defn handle-request [request-map]
  (condp = (:action request-map)
    profile/action (profile/run request-map)
    { :error (str "Unknown action: " (:action request-map)) }))

(defn send-message
  "Sends the given data to the given action in the masques service at the given destination."
  ([destination action]
    (send-message destination action {}))
  ([destination action data]
    (clj-i2p-client/send-message destination service-name (assoc data :action action))))