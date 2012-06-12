(ns masques.controller.login.test.create-user
  (:require [seesaw.core :as seesaw-core]) 
  (:use clojure.test
        masques.controller.login.create-user))

(deftest test-show
  (let [frame (show nil)]
    (is frame)
    (is (.isShowing frame))
    (.doClick (seesaw-core/select frame ["#cancel-button"]))
    (is (not (.isShowing frame)))))