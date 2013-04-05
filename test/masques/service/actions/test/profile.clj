(ns masques.service.actions.test.profile
  (:refer-clojure :exclude [name])
  (:require test.init)
  (:use [masques.service.actions.profile])
  (:use [clojure.test])
  (:require [fixtures.address :as address-fixture]
            [fixtures.email-address :as email-address-fixture]
            [fixtures.friend :as friend-fixture]
            [fixtures.group-membership :as group-membership-fixture]
            [fixtures.group-permission :as group-permission-fixture]
            [fixtures.name :as name-fixture]
            [fixtures.phone-number :as phone-number-fixture]
            [fixtures.user :as user-fixture]
            [masques.model.address :as address-model]
            [masques.model.email-address :as email-model]
            [masques.model.identity :as identity-model] 
            [masques.model.name :as name-model]
            [masques.model.phone-number :as phone-number-model]
            [masques.model.record-utils :as record-utils]
            [masques.model.user :as user-model]
            [masques.test.util :as test-util]))

(test-util/use-combined-login-fixture address-fixture/fixture-map friend-fixture/fixture-map
                                      group-membership-fixture/fixture-map group-permission-fixture/fixture-map
                                      name-fixture/fixture-map email-address-fixture/fixture-map
                                      phone-number-fixture/fixture-map)

(def test-user (record-utils/clean-keys (user-model/clean-private-data (second user-fixture/records))))
(def test-request-map { :user test-user })

(def test-user2 { :id 1 :name "test-user2" :public-key "fail" :public-key-algorithm "RSA" })
(def test-request-map2 { :user test-user2 })

(deftest run-test
  (let [current-address (address-model/first-current-identity-address)]
    (is (= (run test-request-map)
           {:data
             { :name (:name (name-model/first-current-identity-name))
               :email (:email_address (email-model/first-current-identity-email-address))
               :phone-number (:phone_number (phone-number-model/first-current-identity-phone-number))
               :address { :address (:address current-address)
                          :country (:country current-address)
                          :province (:province current-address)
                          :city (:city current-address)
                          :postal-code (:postal_code current-address) } }})))
  (is (= (run test-request-map2) {:data { :name nil :email nil :phone-number nil :address nil }})))
