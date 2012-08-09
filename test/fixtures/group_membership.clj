(ns fixtures.group-membership
  (:require [fixtures.group :as group-fixture]
            [fixtures.friend :as friend-fixture]))

(def fixture-table-name :group-memberships)

(def records [
  { :id 1
    :group_id 1
    :friend_id 1 }])

(def fixture-map { :table fixture-table-name
                   :records records
                   :required-fixtures [group-fixture/fixture-map friend-fixture/fixture-map] })