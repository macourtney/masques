(ns masques.model.test.address
  (:require [fixtures.identity :as fixtures-identity]) 
  (:use clojure.test
        masques.model.address))

(def test-identity (first fixtures-identity/records))

(deftest test-add-address
  (let [address-id (save-or-update-identity-address test-identity "12345 blah st." "USA" "Virginia" "Reston" "20190")]
    (is address-id)
    (let [test-address (get-record address-id)]
      (is test-address)
      (is (= test-address (first-identity-address test-identity))))
    (save-or-update-identity-address test-identity "54321 blah st." "USA" "Virginia" "Reston" "20191")
    (let [test-address (get-record address-id)]
      (is test-address)
      (is (= test-address (first-identity-address test-identity))))))