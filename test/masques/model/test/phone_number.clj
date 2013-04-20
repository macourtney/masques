(ns masques.model.test.phone-number
  (:require [fixtures.identity :as fixtures-identity]) 
  (:use clojure.test
        masques.model.phone-number))

(def test-identity (first fixtures-identity/records))

;(deftest test-add-name
;  (let [phone-number-id (save-or-update-identity-phone-number test-identity "blah")]
;    (is phone-number-id)
;    (try
;      (let [test-phone-number (get-record phone-number-id)]
;        (is test-phone-number)
;        (is (= test-phone-number (first-identity-phone-number test-identity))))
;      (save-or-update-identity-phone-number test-identity "blah2")
;      (let [test-phone-number (get-record phone-number-id)]
;        (is test-phone-number)
;        (is (= test-phone-number (first-identity-phone-number test-identity))))
;      (finally
;        (destroy-record { :id phone-number-id })))))