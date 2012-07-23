(ns masques.service.protocol.test.profile
  (:require test.init)
  (:use [masques.service.actions.profile])
  (:use [clojure.test])
  (:require [fixtures.friend :as friend-fixture]
            [fixtures.user :as user-fixture]
            [masques.model.address :as address-model]
            [masques.model.email-address :as email-model]
            [masques.model.name :as name-model]
            [masques.model.phone-number :as phone-number-model]
            [masques.model.record-utils :as record-utils]
            [masques.model.user :as user-model]
            [masques.test.util :as test-util]))

(test-util/use-combined-login-fixture friend-fixture/fixture-map)

(def test-user (record-utils/clean-keys (user-model/clean-private-data (second user-fixture/records))))
(def test-request-map { :user test-user })

(deftest run-test
  (let [current-address (address-model/first-current-identity-address)]
    (is (= (run test-request-map)
           {:data
             { :name (:name (name-model/first-current-identity-name))
               :email_address (:email_address (email-model/first-current-identity-email-address))
               :phone_number (:phone_number (phone-number-model/first-current-identity-phone-number))
               :address { :address (:address current-address)
                          :country (:country current-address)
                          :province (:province current-address)
                          :city (:city current-address)
                          :postal-code (:postal-code current-address) } }}))))
