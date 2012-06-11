(ns masques.view.add-destination.test.add-destination
  (:use clojure.test
        masques.view.add-destination.add-destination))

(deftest test-create
  (is (create nil)))