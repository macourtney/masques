(ns masques.model.test.album
  (:require test.init)
  (:require [clj-time.core :as clj-time])
  (:use clojure.test
        masques.model.base
        masques.model.album))

#_(defn to-number [d-map cur]
  (read-string (cur d-map)))

(deftest test-add-album
  (let [album-record (save {:name "tester album"})]
    ; (is album-record)
    ; (is (:id album-record))
    ; (is (= (:name album-record) "tester album"))
    ; (instance? DateTime (:created-at album-record))
    ; (is (clj-time/before? (:time-stamp album-record) (clj-time/now)))
  )
)