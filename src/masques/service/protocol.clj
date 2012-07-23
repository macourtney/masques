(ns masques.service.protocol
  (:require [clj-i2p.service :as service]
            [clj-i2p.service-protocol :as service-protocol]
            [masques.service.core :as service-core]))

(deftype MasquesService []
  service-protocol/Service
  (key [service]
     service-core/service-name)

  (name [service]
    "Masques Service")

  (version [service]
    "1.0.0-SNAPSHOT")

  (description [service]
    "This is a service which handles all Masques requests.")

  (handle [service request-map]
    (service-core/handle-request request-map)))

(defn create-service []
  (MasquesService.))

(defn init []
  (service/add-service (create-service)))