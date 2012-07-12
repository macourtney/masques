(ns masques.test.interceptor
  (:require test.init)
  (:use [masques.interceptor])
  (:use [clojure.test])
  (:require [fixtures.friend :as friend-fixture]
            [fixtures.user :as user-fixture]
            [masques.model.record-utils :as record-utils]
            [masques.model.user :as user-model]
            [masques.test.util :as test-util]))

(test-util/use-combined-login-fixture friend-fixture/fixture-map)

(deftest test-interceptor
  (is (= (interceptor identity {})
         { :user (record-utils/clean-keys (user-model/clean-private-data (first user-fixture/records)))})))
