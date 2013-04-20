(ns masques.model.test.name
  (:require [fixtures.identity :as fixtures-identity]
            [fixtures.util :as fixtures-util]) 
  (:use clojure.test
        masques.model.name))

(def test-identity (first fixtures-identity/records))

;(fixtures-util/use-fixture-maps :once fixtures-identity/fixture-map)

;(deftest test-add-name
;  (let [name-id (save-or-update-identity-name test-identity "blah")]
;    (is name-id)
;    (try
;      (let [test-name (get-record name-id)]
;        (is test-name)
;        (is (= test-name (first-identity-name test-identity))))
;      (save-or-update-identity-name test-identity "blah2")
;      (let [test-name (get-record name-id)]
;        (is test-name)
;        (is (= test-name (first-identity-name test-identity))))
;      (finally
;        (destroy-record { :id name-id })))))

;(deftest test-find-name-identity
;  (let [name-str "blah"
;        name-id (save-or-update-identity-name test-identity name-str)]
;    (is name-id)
;    (try
;      (is (= test-identity (find-name-identity name-str))) 
;      (is (= test-identity (find-name-identity name-id)))
;      (is (= test-identity (find-name-identity (get-record name-id))))
;      (try
;        (find-name-identity 1.0)
;        (is false "Expected an exception for an invalid name.")
;        (catch Throwable t)) ; Do nothing. this is the expected result.
;      (finally
;        (destroy-record { :id name-id })))))