(ns masques.model.test.share
  (:require test.init)
  (:require [masques.model.message :as message-model])
  (:use clojure.test
        masques.model.share))

(def ^:dynamic message-record {
                                :subject "The subject, G" 
                                :body "Nice body you got there..."
                              })

(def ^:dynamic share-record { :content-type "message" })

(def ^:dynamic friend-request { :content-type "friend" })

(defn share-test-fixture [function]
  (binding [message-record (message-model/save message-record)]
    (binding [share-record (save (assoc share-record :message-id (:id message-record)))
              friend-request (save friend-request)]
      (function)
      (delete-share friend-request)
      (delete-share share-record))
    (message-model/delete-message message-record)))

(use-fixtures :once share-test-fixture)

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
    (is (map? (:message share-record)))))

(deftest test-receive-share
  (let [message-share (get-and-build (:id share-record))
        friend-share (get-and-build (:id friend-request))]
    (is (receive share-record))))
