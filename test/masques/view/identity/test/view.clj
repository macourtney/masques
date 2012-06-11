(ns masques.view.identity.test.view
  (:use clojure.test
        masques.view.identity.view))

(deftest test-create
  (is (create nil)))