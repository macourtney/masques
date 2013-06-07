(ns masques.model.test.friend-request
  (:require test.init)
  (:use clojure.test
        masques.model.base
        masques.model.friend-request))


(def request-map {:request-status "pending"})

(def big-request {:request-status "pending"
                  :message {:subject "Let's be friends on Masques!"
                            :body "I met you at the party. I had on the blue hat."}
                  :profile {:alias "Ted"}})

(defn request-tester [request-record]
  (is request-record)
  (is (:id request-record))
  (is (integer? (:id request-record)))
  (is (= (:request-status request-record) "pending"))
  (is (instance? org.joda.time.DateTime (:created-at request-record))))

(deftest test-save-request
  (let [request-record (save request-map)]
    (request-tester request-record)))

(deftest test-find-request
  (let [request-record (save request-map)]
    (request-tester request-record)))

