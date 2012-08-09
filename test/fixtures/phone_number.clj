(ns fixtures.phone-number
  (:require [fixtures.identity :as identity-fixture]))

(def fixture-table-name :phone_numbers)

(def records [
  { :id 1
    :identity_id 1
    :phone_number "123-456-7890" }
  { :id 2
    :identity_id 2
    :phone_number "098-765-4321" }])

(def fixture-map { :table fixture-table-name :records records :required-fixtures [identity-fixture/fixture-map] })