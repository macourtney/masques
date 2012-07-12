(ns masques.service.request-map-utils
  (:require test.init)
  (:use [masques.service.request-map-utils])
  (:use [clojure.test])
  (:require [fixtures.friend :as friend-fixture]
            [fixtures.identity :as identity-fixture] 
            [fixtures.user :as user-fixture]
            [masques.test.util :as test-util]))

(test-util/use-combined-login-fixture friend-fixture/fixture-map)

(def test-user (record-utils/clean-keys (select-keys (second user-fixture/records) [:name :public_key :public_key_algorithm])))
(def test-request-map { :user test-user })

(deftest test-sender-identity
  (is (= (sender-identity test-request-map) (first identity-fixture/records))))