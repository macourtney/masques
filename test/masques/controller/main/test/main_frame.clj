(ns masques.controller.main.test.main-frame
  (:require ;[fixtures.identity :as identity-fixture]
            ;[fixtures.user :as user-fixture]
            ;[fixtures.util :as fixtures-util]
            ;[masques.model.friend :as friend-model]
            ;[masques.model.identity :as identity-model]
            ;[masques.model.peer :as peer-model]
            ;[masques.test.util :as test-util]
            [seesaw.core :as seesaw-core])
  (:use clojure.test
        masques.controller.main.main-frame))

;(test-util/use-combined-login-fixture identity-fixture/fixture-map)

(defn assert-listener-count [test-count]
  ;(is (= (friend-model/friend-add-listener-count) test-count))
  ;(is (= (friend-model/friend-delete-listener-count) test-count))
  ;(is (= (identity-model/identity-add-listener-count) test-count))
  ;(is (= (identity-model/identity-update-listener-count) test-count))
  ;(is (= (identity-model/identity-delete-listener-count) test-count))
  ;(is (= (peer-model/peer-update-listener-count) test-count))
  ;(is (= (peer-model/peer-delete-listener-count) test-count))
  )

(defn assert-no-listeners []
  (assert-listener-count 0))

(defn assert-one-listener-each []
  (assert-listener-count 1))

(deftest test-show
  ;(assert-no-listeners)
  (let [frame (show)]
    (Thread/sleep 100000)
    (is frame)
    (is (.isShowing frame))
    ;(assert-one-listener-each)
    (.setVisible frame false)
    (.dispose frame)
    (Thread/sleep 100)
    (is (not (.isShowing frame)))
    ;(assert-no-listeners)
    ))