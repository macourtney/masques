(ns masques.service.protocol
  (:require [clj-i2p.service :as service]
            [clj-i2p.service-protocol :as service-protocol]
            [masques.service.actions.profile :as profile]
            [masques.service.actions.request-friendship
              :as request-friendship]))

(def service-name :masques-service)
(def service-version "1.0.0-SNAPSHOT")

(def actions
  { profile/action profile/run
    request-friendship/action request-friendship/run })

(deftype MasquesService []
  service-protocol/Service
  (key [service] service-name)

  (name [service] "Masques Service")

  (version [service] service-version)

  (description [service]
    "This is a service which handles all Masques requests.")

  (handle [service request-map]
    (let [action (:action request-map)]
      (if-let [run-function (get actions action)]
        (run-function request-map)
        { :error (str "Unknown action: " action) }))
    ;(condp = (:action request-map)
    ;  profile/action (profile/run request-map)
    ;  request-friendship/action (request-friendship/run request-map)
    ;  { :error (str "Unknown action: " (:action request-map)) })
    ))

(def masques-service (MasquesService.))

(defn init []
  (service/add-service masques-service))