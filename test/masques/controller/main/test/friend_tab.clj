(ns masques.controller.main.test.friend-tab
  (:require [fixtures.friend :as friend-fixture]
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
  (let [frame (test-util/show (friend-tab-view/create))]
    (Thread/sleep 100)
    (is frame)
    (is (.isShowing frame))
    ;(assert-one-listener-each)
    (.setVisible frame false)
    (.dispose frame)
    (Thread/sleep 100)
    (is (not (.isShowing frame)))
    ;(assert-no-listeners)
    ))