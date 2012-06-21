(ns fixtures.friend
  (:require [fixtures.identity :as identity-fixture]))

(def records [
  { :id 1
    :identity_id 1
    :friend_id 2 }])

(def fixture-table-name :friends)

(def fixture-map { :table fixture-table-name :records records :required-fixtures [identity-fixture/fixture-map] })