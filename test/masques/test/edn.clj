(ns masques.test.edn
  (:refer-clojure :exclude [read read-string])
  (:use masques.edn
        clojure.test))

(deftest test-write-string
  (is (write-string { :foo "bar" }) "{ :foo \"bar\" }"))

(deftest test-read
  (is (read-string "{ :foo \"bar\" }") { :foo "bar" }))