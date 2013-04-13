(ns masques.model.test.share
  (:require test.init)
  (:use clojure.test
        masques.model.base
        masques.model.share))

(def share-map { :content-type "message" :message-id 42 } )

(deftest test-add-share
  (let [share-record (save share-map)]
    ; (is share-record)
    ; (is (:id share-record))
    ; (is (= (:content-type share-record) "message"))
    ; (is (= (:message-id share-record) 42))
    ; (is (not-nil? (:uuid share-record)))
    ; (is (instance? org.joda.time.DateTime (:created-at share-record)))
    ))