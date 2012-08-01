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
    :type "write" }])

(def fixture-map { :table fixture-table-name
                   :records records
                   :required-fixtures [group-fixture/fixture-map permission-fixture/fixture-map] })