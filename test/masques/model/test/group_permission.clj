(ns masques.model.test.group-permission
  (:require [fixtures.group :as fixtures-group]
            [fixtures.permission :as fixtures-permission]
            [fixtures.util :as fixtures-util]) 
  (:use clojure.test
        masques.model.group-permission))

(def test-group (first fixtures-group/records))

(def test-permission (first fixtures-permission/records))

(fixtures-util/use-fixture-maps :once [fixtures-permission/fixture-map fixtures-group/fixture-map])

(deftest test-valid-type?
  (is (valid-type? read-type))
  (is (valid-type? write-type))
  (is (valid-type? none-type))
  (is (not (valid-type? "fail")))
  (is (not (valid-type? nil))))

(defn test-none-permission [test-group-id test-permission-id permission-type]
  (if (= permission-type none-type)
    (do
      (is (has-none-permission? test-group-id test-permission-id))
      (is (any-group-has-none-permission? [test-group-id] test-permission-id)))
    (do
      (is (not (has-none-permission? test-group-id test-permission-id)))
      (is (not (any-group-has-none-permission? [test-group-id] test-permission-id))))))

(defn test-read-permission [test-group-id test-permission-id permission-type]
  (if (= permission-type read-type)
    (do
      (is (has-read-permission? test-group-id test-permission-id))
      (is (any-group-has-read-permission? [test-group-id] test-permission-id)))
    (do
      (is (not (has-read-permission? test-group-id test-permission-id)))
      (is (not (any-group-has-read-permission? [test-group-id] test-permission-id))))))

(defn test-write-permission [test-group-id test-permission-id permission-type]
  (if (= permission-type write-type)
    (do
      (is (has-write-permission? test-group-id test-permission-id))
      (is (any-group-has-write-permission? [test-group-id] test-permission-id)))
    (do
      (is (not (has-write-permission? test-group-id test-permission-id)))
      (is (not (any-group-has-write-permission? [test-group-id] test-permission-id))))))

(defn test-permission-types [test-group-id test-permission-id permission-type]
  (test-none-permission test-group-id test-permission-id permission-type)
  (test-read-permission test-group-id test-permission-id permission-type)
  (test-write-permission test-group-id test-permission-id permission-type))

(defn test-group-permission [group-permission-id test-group-id test-permission-id permission-type]
  (let [test-group-permission (get-record group-permission-id)]
    (is test-group-permission)
    (is (:group_id test-group-permission) test-group-id)
    (is (:permission_id test-group-permission) test-permission-id)
    (is (:type test-group-permission) permission-type)))

;(deftest test-add-and-remove-read-permission-from-group
;  (let [test-group-id (:id test-group)
;        test-permission-id (:id test-permission)
;        group-permission-id (add-read-permission-to-group test-group-id test-permission-id)]
;    (is group-permission-id)
;    (try
;      (test-group-permission group-permission-id test-group-id test-permission-id read-type)
;      (test-permission-types test-group-id test-permission-id read-type)
;      (remove-read-permission-from-group test-group-id test-permission-id)
;      (test-permission-types test-group-id test-permission-id nil)
;      (finally
;        (destroy-record { :id group-permission-id })))))

;(deftest test-add-and-remove-write-permission-from-group
;  (let [test-group-id (:id test-group)
;        test-permission-id (:id test-permission)
;        group-permission-id (add-write-permission-to-group test-group-id test-permission-id)]
;    (is group-permission-id)
;    (try
;      (test-group-permission group-permission-id test-group-id test-permission-id write-type)
;      (test-permission-types test-group-id test-permission-id write-type)
;      (remove-write-permission-from-group test-group-id test-permission-id)
;      (test-permission-types test-group-id test-permission-id nil)
;      (finally
;        (destroy-record { :id group-permission-id })))))

;(deftest test-add-and-remove-none-permission-from-group
;  (let [test-group-id (:id test-group)
;        test-permission-id (:id test-permission)
;        group-permission-id (add-none-permission-to-group test-group-id test-permission-id)
;        group-permission-id2 (add-read-permission-to-group test-group-id test-permission-id)]
;    (is group-permission-id)
;    (is group-permission-id2)
;    (try
;      (test-group-permission group-permission-id test-group-id test-permission-id none-type)
;      (test-permission-types test-group-id test-permission-id none-type)
;      (remove-none-permission-from-group test-group-id test-permission-id)
;      (test-permission-types test-group-id test-permission-id read-type)
;      (remove-read-permission-from-group test-group-id test-permission-id)
;      (test-permission-types test-group-id test-permission-id nil)
;      (finally
;        (destroy-record { :id group-permission-id })
;        (destroy-record { :id group-permission-id2 })))))