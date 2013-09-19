(ns masques.model.test.grouping
  (:require test.init)
  (:use clojure.test
        masques.model.base
        masques.model.grouping)
  #_(:require [masques.model.grouping-profile :as grouping-profile-model]))


(def grouping-map {:name "tester grouping"})

(deftest test-add-grouping
  (let [grouping-record (save grouping-map)]
    (println grouping-record)
    (is grouping-record)
    (is (:id grouping-record))
    (is (= (:name grouping-record) "tester grouping"))
    (is (instance? org.joda.time.DateTime (:created-at grouping-record)))))

#_(deftest test-add-album-with-files
  (let [file-1 (file-model/save {:name "thing one" :album-id (:id album-record)})
        file-2 (file-model/save {:name "thing two" :album-id (:id album-record)})
        album-and-files (with-files (:id album-record))]
    (is file-1)
    (is file-2)
    (is (:files album-and-files))))