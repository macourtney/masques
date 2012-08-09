(ns fixtures.group-permission
  (:require [fixtures.group :as group-fixture]
            [fixtures.permission :as permission-fixture]))

(def fixture-table-name :group_permissions)

(def records [
  { :id 1
    :group_id 1
    :permission_id 1
    :type "read" }
  { :id 2
    :group_id 1
    :permission_id 1
    :type "write" }
  { :id 3
    :group_id 1
    :permission_id 4
    :type "read" }
  { :id 4
    :group_id 1
    :permission_id 5
    :type "read" }
  { :id 5
    :group_id 1
    :permission_id 6
    :type "read" }
  { :id 6
    :group_id 1
    :permission_id 7
    :type "read" }
  { :id 7
    :group_id 1
    :permission_id 8
    :type "read" }
  { :id 8
    :group_id 1
    :permission_id 9
    :type "read" }
  { :id 9
    :group_id 1
    :permission_id 10
    :type "read" }
  { :id 10
    :group_id 1
    :permission_id 11
    :type "read" }])

(def fixture-map { :table fixture-table-name
                   :records records
                   :required-fixtures [group-fixture/fixture-map permission-fixture/fixture-map] })