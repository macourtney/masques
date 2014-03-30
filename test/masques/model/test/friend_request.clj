(ns masques.model.test.friend-request
  (:require test.init
            [clojure.java.io :as io]
            [masques.model.profile :as profile]
            [masques.model.share :as share]
            [masques.test.util :as test-util])
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
    (request-tester request-record)
    (delete-friend-request request-record)))

(deftest test-find-request
  (let [request-record (save request-map)]
    (request-tester request-record)
    (delete-friend-request request-record)))

(deftest test-send-request
  (is (= 0 (count-pending-requests)))
  (profile/create-masques-id-file test-util/test-masques-id-file
                                  test-util/profile-map)
  (let [friend-request-share (send-request test-util/test-masques-id-file
                                           "test message")
        friend-request (share/get-content friend-request-share)]
    (is friend-request)
    (is (= 1 (count-pending-requests)))
    (is (= (select-keys friend-request [:id :profile-id]) (pending-request 0)))
    (is (= (request-status-key friend-request) pending-status))
    (is (not (requested-at-key friend-request)))
    (let [profile-id (profile-id-key friend-request)]
      (is profile-id)
      (is (profile/find-profile profile-id)))
    (delete-friend-request friend-request)
    (share/delete-share friend-request-share))
  (io/delete-file test-util/test-masques-id-file))