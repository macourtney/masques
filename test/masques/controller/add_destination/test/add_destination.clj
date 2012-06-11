(ns masques.controller.add-destination.test.add-destination
  (:require [seesaw.core :as seesaw-core]) 
  (:use clojure.test
        masques.controller.add-destination.add-destination))

(deftest test-create
  (let [frame (show nil nil)]
    (is frame)
    (is (.isShowing frame))
    (.doClick (seesaw-core/select frame ["#cancel-button"]))
    (is (not (.isShowing frame)))))