(ns masques.view.login.test.create-user
  (:use clojure.test
        masques.view.login.create-user))

(deftest test-create
  (is (create nil)))