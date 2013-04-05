(ns masques.view.friend.test.view
  (:refer-clojure :exclude [load])
  (:use clojure.test
        masques.view.friend.view))

(deftest test-create
  (is (create nil)))