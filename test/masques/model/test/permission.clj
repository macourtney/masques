(ns masques.model.test.permission
  (:require [test.init :as test-init])
  (:require [masques.test.util :as test-util]) 
  (:use clojure.test
        masques.model.permission))

(def test-permission-name "permission1")

(def test-permission { :name test-permission-name })

(deftest test-find-permission
  (let [permission-id (insert test-permission)
        inserted-permission (assoc test-permission :id permission-id)]
    (is permission-id)
    (try
      (is (= inserted-permission (find-permission test-permission-name)))
      (is (= inserted-permission (find-permission inserted-permission)))
      (is (= inserted-permission (find-permission test-permission)))
      (is (= inserted-permission (find-permission permission-id)))
      (try
        (find-permission 1.0)
        (is false "Expected an exception for an invalid permission.")
        (catch Throwable t)) ; Do nothing. this is the expected result.
      (finally
        (destroy-record { :id permission-id })))))

(deftest test-permission-id
  (let [permission-record-id (insert test-permission)
        inserted-permission (assoc test-permission :id permission-record-id)]
    (is permission-record-id)
    (try
      (is (= permission-record-id (permission-id test-permission-name)))
      (is (= permission-record-id (permission-id inserted-permission)))
      (is (= permission-record-id (permission-id test-permission)))
      (is (= permission-record-id (permission-id permission-record-id)))
      (try
        (permission-id 1.0)
        (is false "Expected an exception for an invalid permission.")
        (catch Throwable t)) ; Do nothing. this is the expected result.
      (finally
        (destroy-record { :id permission-record-id })))))

(deftest test-find-permission
  (let [permission-id (insert test-permission)
        inserted-permission (assoc test-permission :id permission-id)]
    (is permission-id)
    (try
      (is (= (find-permissions [permission-id]) [inserted-permission])) 
      (is (= (find-permissions [-1]) []))
      (try
        (find-permission 1.0)
        (is false "Expected an exception for an invalid permission.")
        (catch Throwable t)) ; Do nothing. this is the expected result.
      (finally
        (destroy-record { :id permission-id })))))