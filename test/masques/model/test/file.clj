(ns masques.model.test.file
  (:require test.init)
  (:use clojure.test
        masques.model.file))

(deftest test-add-file-record
  (let [file-record (save { :path "/Users/Ted/masques/avatar.png" })]
    (is file-record)
    (is (:id file-record))
    (is (= (:path file-record) "/Users/Ted/masques/avatar.png"))
    (is (instance? org.joda.time.DateTime (:created-at file-record)))))

(deftest test-copy-file
  (let [src "/Users/Ted/test.txt"
        dest "/Users/Ted/test_result.txt"]
    (copy src dest)))

