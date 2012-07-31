(ns masques.model.test.group
  (:require [test.init :as test-init]) 
  (:use clojure.test
        masques.model.group))

(def test-group-name "group1")

(def test-group { :name test-group-name })

(deftest test-find-group
  (let [group-id (insert test-group)
        inserted-group (assoc test-group :id group-id)]
    (is group-id)
    (try
      (is (= inserted-group (find-group test-group-name)))
      (is (= inserted-group (find-group inserted-group)))
      (is (= inserted-group (find-group test-group)))
      (is (= inserted-group (find-group group-id)))
      (try
        (find-group 1.0)
        (is false "Expected an exception for an invalid group.")
        (catch Throwable t)) ; Do nothing. this is the expected result.
      (finally
        (destroy-record { :id group-id })))))

(deftest test-group-id
  (let [group-record-id (insert test-group)
        inserted-group (assoc test-group :id group-record-id)]
    (is group-record-id)
    (try
      (is (= group-record-id (group-id test-group-name)))
      (is (= group-record-id (group-id inserted-group)))
      (is (= group-record-id (group-id test-group)))
      (is (= group-record-id (group-id group-record-id)))
      (try
        (group-id 1.0)
        (is false "Expected an exception for an invalid group.")
        (catch Throwable t)) ; Do nothing. this is the expected result.
      (finally
        (destroy-record { :id group-record-id })))))