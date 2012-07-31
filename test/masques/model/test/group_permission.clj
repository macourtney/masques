(ns masques.model.test.group-permission
  (:require [fixtures.group :as fixtures-group]
            [fixtures.permission :as fixtures-permission]
            [fixtures.util :as fixtures-util]) 
  (:use clojure.test
        masques.model.group-permission))

(def test-group (first fixtures-group/records))

(def test-permission (first fixtures-permission/records))

(fixtures-util/use-fixture-maps :once [fixtures-permission/fixture-map fixtures-group/fixture-map])

(deftest test-add-and-remove-permission-from-group
  (let [group-permission-id (add-permission-to-group test-group test-permission)]
    (is group-permission-id)
    (try
      (let [test-group-permission (get-record group-permission-id)]
        (is test-group-permission)
        (is (:group_id test-group-permission) (:id test-group))
        (is (:permission_id test-group-permission) (:id test-permission)))
      (is (has-permission? test-group test-permission))
      (remove-permission-from-group test-group test-permission)
      (is (not (has-permission? test-group test-permission)))
      (finally
        (destroy-record { :id group-permission-id })))))