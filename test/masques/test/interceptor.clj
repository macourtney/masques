(ns masques.test.interceptor
  (:require test.init)
  (:use [masques.interceptor])
  (:use [clojure.test])
  (:require [fixtures.friend :as friend-fixture]
            [fixtures.user :as user-fixture]
            ;[masques.model.record-utils :as record-utils]
            ;[masques.model.user :as user-model]
            [masques.model.profile :as profile-model]
            [masques.test.util :as test-util]))

(test-util/use-combined-login-fixture)

(deftest test-interceptor
  (is (= (interceptor identity {})
         { :user (profile-model/clean-user-data) })))
