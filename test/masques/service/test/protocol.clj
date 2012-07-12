(ns masques.service.test.protocol
  (:use [masques.service.protocol])
  (:use [clojure.test])
  (:require [clj-i2p.service-protocol :as service-protocol]))

(deftest test-service
  (let [service (create-service)]
    (is (= :masques-service (service-protocol/key service)))
    (is (= "Masques Service" (service-protocol/name service)))
    (is (= "1.0.0-SNAPSHOT" (service-protocol/version service)))
    (is (= "This is a service which handles all Masques requests." (service-protocol/description service)))))
