(ns masques.model.test.album
  (:require test.init)
  (:use clojure.test
        masques.model.base
        masques.model.album)
  (:require [masques.model.file :as file-model]))


(def album-map {:name "tester album" :comments "This album rocks, yo!" :size-of-all-files 4000})

(deftest test-add-album
  (let [album-record (save album-map)]
    (is album-record)
    (is (:id album-record))
    (is (= (:name album-record) "tester album"))
    (is (= (:comments album-record) "This album rocks, yo!"))
    (is (= (:size-of-all-files album-record) 4000))
    (is (instance? org.joda.time.DateTime (:created-at album-record)))))


#_(deftest test-add-album-with-files
  (let [file-1 (file-model/save {:name "thing one" :album-id (:id album-record)})
        file-2 (file-model/save {:name "thing two" :album-id (:id album-record)})
        album-and-files (with-files (:id album-record))]
    (is file-1)
    (is file-2)
    (is (:files album-and-files))))