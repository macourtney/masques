(ns masques.model.test.email-address
  (:require [fixtures.identity :as fixtures-identity]) 
  (:use clojure.test
        masques.model.email-address))

(def test-identity (first fixtures-identity/records))

;(deftest test-add-email-address
;  (let [address-id (save-or-update-identity-email-address test-identity "sample@example.com")]
;    (is address-id)
;    (try
;      (let [test-address (get-record address-id)]
;        (is test-address)
;        (is (= test-address (first-identity-email-address test-identity))))
;      (save-or-update-identity-email-address test-identity "sample@example.com")
;      (let [test-address (get-record address-id)]
;        (is test-address)
;        (is (= test-address (first-identity-email-address test-identity))))
;      (finally
;        (destroy-record { :id address-id })))))