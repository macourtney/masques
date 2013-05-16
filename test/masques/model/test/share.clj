(ns masques.model.test.share
  (:require test.init)
  (:require [masques.model.message :as message-model])
  (:use clojure.test
        masques.model.share))

(def message-record (message-model/save {
  :subject "The subject, G" 
  :body "Nice body you got there..."
}))

(def share-record (save {
  :content-type "message"
  :message-id (:id message-record)
}))

(deftest test-add-share
  (is share-record)
  (is (:id share-record))
  (is (= (:content-type share-record) "message"))
  (is (:message-id share-record))
  (is (not (nil? (:uuid share-record))))
  (is (instance? org.joda.time.DateTime (:created-at share-record))))

(deftest test-load-share-with-content
  (let [share-record (get-and-build (:id share-record))]
    (is (map? share-record))
    ;(is (map? (:message share-record)))
    ))
