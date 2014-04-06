(ns masques.service.actions.test.request-friendship
  (:require test.init)
  (:use [masques.service.actions.request-friendship])
  (:use [clojure.test])
  (:require [masques.model.friend-request :as friend-request-model]
            [masques.model.message :as message-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.test.util :as test-util]))

(def test-message "test-message")
(def test-profile
  (select-keys test-util/profile-map 
    [profile-model/alias-key profile-model/identity-key
     profile-model/identity-algorithm-key]))

(def test-request-map { :data { :message test-message :profile test-profile } })

(deftest run-test
  (is (run test-request-map))
  (is (= (friend-request-model/count-pending-received-requests) 1))
  (let [friend-request (friend-request-model/pending-received-request 0)
        friend-request-share
          (share-model/find-friend-request-share friend-request)]
    (is friend-request)
    (is friend-request-share)
    (is (profile-model/find-profile
          (friend-request-model/profile-id-key friend-request)))
    (is (= (message-model/body (share-model/message-id friend-request-share))
           test-message))
    (share-model/delete-share friend-request-share)))