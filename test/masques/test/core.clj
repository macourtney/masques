(ns masques.test.core
  (:use [masques.core])
  (:use [clojure.test]))

(deftest test-init
  (is (init-args ["-m" "test"])))
