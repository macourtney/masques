(ns masques.controller.main.test.friend-tab
  (:require [fixtures.friend :as friend-fixture]
            [masques.model.friend :as friend-model]
            [masques.model.identity :as identity-model]
            [masques.test.util :as test-util]
            [masques.view.main.friend-tab :as friend-tab-view]
            [seesaw.core :as seesaw-core])
  (:use clojure.test
        masques.controller.main.friend-tab))

(test-util/use-combined-login-fixture friend-fixture/fixture-map)

(defn assert-listener-count [test-count]
  (is (= (friend-model/friend-add-listener-count) test-count)) 
  (is (= (friend-model/friend-delete-listener-count) test-count)))

(defn assert-no-listeners []
  (assert-listener-count 0))

(defn assert-one-listener-each []
  (assert-listener-count 1))

(deftest test-show
  (assert-no-listeners)
  (let [frame (test-util/assert-show (friend-tab-view/create) init)]
    ;(Thread/sleep 10000)
    (is (= (friend-count frame) 1))
    (is (= (friend-xml-text frame) (friend-model/friend-xml-string)))
    (assert-one-listener-each)
    (let [friend-identity (identity-model/get-record 3)]
      (friend-model/add-friend friend-identity)
      (is (= (friend-count frame) 2))
      (friend-model/remove-friend friend-identity)
      (is (= (friend-count frame) 1)))
    ;(Thread/sleep 10000)
    (test-util/assert-close frame)
    (assert-no-listeners)))