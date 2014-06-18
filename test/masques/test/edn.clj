(ns masques.test.edn
  (:refer-clojure :exclude [read read-string])
  (:require [clojure.java.io :as io])
  (:use masques.edn
        clojure.test)
  (:import [java.io IOException]))

(def test-edn "./test/support_files/test.edn")

(deftest test-write-string
  (is (write-string { :foo "bar" }) "{ :foo \"bar\" }"))

(deftest test-read-sring
  (is (read-string "{ :foo \"bar\" }") { :foo "bar" }))

(deftest test-read
  (let [test-edn-reader (io/reader (io/file test-edn))]
    (is (read test-edn-reader) { :foo "bar" })
    (try
      (.ready test-edn-reader)
      (is false "The edn reader was not closed.")
      
      ; IOException thrown if the reader is already closed which it should be at
      ; this point.
      (catch IOException e))))

(deftest test-write
  (let [test-edn-file (io/file "./test/support_files/test_out.edn")
        test-edn-writer (io/writer test-edn-file)
        test-form { :foo "bar" }]
    (write test-edn-writer test-form)
    (is (read test-edn-file) test-form)
    (try
      (.flush test-edn-writer)
      (is false "The edn writer was not closed.")
      
      ; IOException thrown if the writer is already closed which it should be at
      ; this point.
      (catch IOException e))
    (io/delete-file test-edn-file)))