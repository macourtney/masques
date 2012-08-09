(ns fixtures.address
  (:require [fixtures.identity :as identity-fixture]))

(def fixture-table-name :addresses)

(def records [
  { :id 1
    :identity_id 1
    :address "1234 S St."
    :country "US" 
    :province "VA" 
    :city "Reston" 
    :postal_code "20190" }
  { :id 2
    :identity_id 2
    :address "1234 N St."
    :country "US" 
    :province "VA" 
    :city "Reston" 
    :postal_code "20191" }])

(def fixture-map { :table fixture-table-name :records records :required-fixtures [identity-fixture/fixture-map] })