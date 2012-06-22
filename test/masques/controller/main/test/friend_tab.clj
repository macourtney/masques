(ns masques.controller.main.test.friend-tab
  (:require [fixtures.friend :as friend-fixture]
            [masques.model.friend :as friend-model]
            [masques.test.util :as test-util]
            [masques.view.main.friend-tab :as friend-tab-view]
            [seesaw.core :as seesaw-core])
  (:use clojure.test
        masques.controller.main.friend-tab))

(test-util/use-combined-login-fixture friend-fixture/fixture-map)

;(defn assert-no-listeners []
;  (assert-listener-count 0))

;(defn assert-one-listener-each []
;  (assert-listener-count 1))

(deftest test-show
  ;(assert-no-listeners)
  (let [frame (test-util/assert-show (friend-tab-view/create))]
    (init frame)
    ;(Thread/sleep 10000)
    (is (= (friend-count frame) 1))
    ;(assert-one-listener-each)
    (test-util/assert-close frame)
    ;(assert-no-listeners)
    ))