(ns masques.controller.main.test.friend-tab
  (:require [fixtures.friend :as friend-fixture]
            [masques.model.friend :as friend-model]
            [masques.test.util :as test-util]
            [masques.view.main.friend-tab :as friend-tab-view]
            [seesaw.core :as seesaw-core])
  (:use clojure.test
        masques.controller.main.friend-tab))

(test-util/use-combined-login-fixture friend-fixture/fixture-map)

(defn assert-listener-count [test-count]
  ;(is (= (peer-model/peer-update-listener-count) test-count)) 
  ;(is (= (peer-model/peer-delete-listener-count) test-count))
  ;(is (= (identity-model/identity-add-listener-count) test-count))
  ;(is (= (identity-model/identity-update-listener-count) test-count))
  ;(is (= (identity-model/identity-delete-listener-count) test-count))
  )

(defn assert-no-listeners []
  (assert-listener-count 0))

(defn assert-one-listener-each []
  (assert-listener-count 1))

(deftest test-show
  ;(assert-no-listeners)
  (let [frame (test-util/assert-show (friend-tab-view/create))]
    (init frame)
    ;(Thread/sleep 10000)
    (is (= (friend-count frame) 1))
    (is (= (friend-xml-text frame) (friend-model/friend-xml-string)))
    (assert-one-listener-each)
    (test-util/assert-close frame)
    (assert-no-listeners)))