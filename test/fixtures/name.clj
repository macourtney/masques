(ns fixtures.name
  (:require [fixtures.identity :as identity-fixture]))

(def fixture-table-name :names)

(def records [
  { :id 1
    :identity_id 1
    :name "blah" }
  { :id 2
    :identity_id 2
    :name "blah2" }])

(def fixture-map { :table fixture-table-name :records records :required-fixtures [identity-fixture/fixture-map] })