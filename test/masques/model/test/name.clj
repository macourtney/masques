(ns masques.model.test.name
  (:require [fixtures.identity :as fixtures-identity]) 
  (:use clojure.test
        masques.model.name))

(def test-identity (first fixtures-identity/records))

(deftest test-add-name
  (let [name-id (save-or-update-identity-name test-identity "blah")]
    (is name-id)
    (try
      (let [test-name (get-record name-id)]
        (is test-name)
        (is (= test-name (first-identity-name test-identity))))
      (save-or-update-identity-name test-identity "blah2")
      (let [test-name (get-record name-id)]
        (is test-name)
        (is (= test-name (first-identity-name test-identity))))
      (finally
        (destroy-record { :id name-id })))))