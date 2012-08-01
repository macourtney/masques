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

(deftest test-add-and-remove-read-permission-from-group
  (let [test-group-id (:id test-group)
        test-permission-id (:id test-permission)
        group-permission-id (add-read-permission-to-group test-group-id test-permission-id)]
    (is group-permission-id)
    (try
      (let [test-group-permission (get-record group-permission-id)]
        (is test-group-permission)
        (is (:group_id test-group-permission) test-group-id)
        (is (:permission_id test-group-permission) test-permission-id)
        (is (:type test-group-permission) read-type))
      (is (has-read-permission? test-group-id test-permission-id))
      (is (any-group-has-read-permission? [test-group-id] test-permission-id))
      (is (not (has-write-permission? test-group-id test-permission-id)))
      (is (not (any-group-has-write-permission? [test-group-id] test-permission-id)))
      (remove-read-permission-from-group test-group-id test-permission-id)
      (is (not (has-read-permission? test-group-id test-permission-id)))
      (is (not (any-group-has-read-permission? [test-group-id] test-permission-id)))
      (is (not (has-write-permission? test-group-id test-permission-id)))
      (is (not (any-group-has-write-permission? [test-group-id] test-permission-id)))
      (finally
        (destroy-record { :id group-permission-id })))))

(deftest test-add-and-remove-write-permission-from-group
  (let [test-group-id (:id test-group)
        test-permission-id (:id test-permission)
        group-permission-id (add-write-permission-to-group test-group-id test-permission-id)]
    (is group-permission-id)
    (try
      (let [test-group-permission (get-record group-permission-id)]
        (is test-group-permission)
        (is (:group_id test-group-permission) test-group-id)
        (is (:permission_id test-group-permission) test-permission-id)
        (is (:type test-group-permission) write-type))
      (is (not (has-read-permission? test-group-id test-permission-id)))
      (is (not (any-group-has-read-permission? [test-group-id] test-permission-id)))
      (is (has-write-permission? test-group-id test-permission-id))
      (is (any-group-has-write-permission? [test-group-id] test-permission-id))
      (remove-write-permission-from-group test-group-id test-permission-id)
      (is (not (has-read-permission? test-group-id test-permission-id)))
      (is (not (any-group-has-read-permission? [test-group-id] test-permission-id)))
      (is (not (has-write-permission? test-group-id test-permission-id)))
      (is (not (any-group-has-write-permission? [test-group-id] test-permission-id)))
      (finally
        (destroy-record { :id group-permission-id })))))