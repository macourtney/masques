(ns masques.model.test.file
  (:require test.init)
  (:use clojure.test
        masques.model.file))

(def avatar-image "./test/support_files/avatar.png")
(def src "./test/support_files/test.txt")
(def dest "./test/support_files/test_result.txt")

(deftest test-add-file-record
  (let [file-record (save { :path avatar-image })]
    (is file-record)
    (is (:id file-record))
    (is (= (:path file-record) avatar-image))
    (is (instance? org.joda.time.DateTime (:created-at file-record)))))

(deftest test-copy-file
  (copy src dest))

