(ns fixtures.email-address
  (:require [fixtures.identity :as identity-fixture]))

(def fixture-table-name :email_addresses)

(def records [
  { :id 1
    :identity_id 1
    :email_address "blah@example.com" }
  { :id 2
    :identity_id 2
    :email_address "blah2@example.com" }])

(def fixture-map { :table fixture-table-name :records records :required-fixtures [identity-fixture/fixture-map] })