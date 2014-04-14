(ns masques.service.core
  (:require [clj-i2p.client :as clj-i2p-client]
            [masques.service.protocol :as service-protocol]))

(defn send-message
  "Sends the given data to the given action in the masques service at the given
destination."
  ([destination action]
    (send-message destination action {}))
  ([destination action data]
    (clj-i2p-client/send-message destination service-protocol/masques-service
      (assoc data :action action))))