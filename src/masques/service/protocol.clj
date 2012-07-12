(ns masques.service.protocol
  (:require [clj-i2p.service :as service]
            [clj-i2p.service-protocol :as service-protocol]
            [masques.service.actions.profile :as profile]))

(defn handle-request [request-map]
  (condp = (:action request-map)
    profile/action (profile/run request-map)
    { :error (str "Unknown action: " (:action request-map)) }))

(deftype MasquesService []
  service-protocol/Service
  (key [service]
     :masques-service)

  (name [service]
    "Masques Service")

  (version [service]
    "1.0.0-SNAPSHOT")

  (description [service]
    "This is a service which handles all Masques requests.")

  (handle [service request-map]
    (handle-request request-map)))

(defn create-service []
  (MasquesService.))

(defn init []
  (service/add-service (create-service)))