(ns masques.service.test.protocol
  (:require test.init)
  (:use masques.service.protocol
        clojure.test)
  (:require [clj-i2p.service-protocol :as service-protocol]))

(deftest test-service
  (is (= :masques-service (service-protocol/key masques-service)))
  (is (= "Masques Service" (service-protocol/name masques-service)))
  (is (= "1.0.0-SNAPSHOT" (service-protocol/version masques-service)))
  (is (= "This is a service which handles all Masques requests."
         (service-protocol/description masques-service))))
