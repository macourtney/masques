(ns masques.service.calls.test.request-friendship
  (:require test.init)
  (:use [masques.service.calls.request-friendship])
  (:use [clojure.test])
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as logging]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.service.actions.request-friendship
              :as request-friendship-action]
            [masques.service.core :as service]
            [masques.test.util :as test-util]))

(def network-destination (atom nil))
(def network-data (atom nil))

(def test-message "test message" )

(defn save-mock-network [destination data]
  (reset! network-destination destination)
  (reset! network-data data)
  { :data { request-friendship-action/received-key true } })

(test-util/use-combined-login-fixture
  (test-util/create-mock-network-fixture save-mock-network))

(deftest request-friendship-test
  (is (nil? @network-destination))
  (is (nil? @network-data))
  (profile-model/create-masques-id-file test-util/test-masques-id-file
                                        test-util/profile-map)
  (let [test-message "test message"
        request-share (send-friend-request test-util/test-masques-id-file
                                           test-message)
;          (friend-request-model/send-request test-util/test-masques-id-file
;                                       test-message)
          ]
;    (is (nil? (friend-request-model/requested-at
;                (share-model/get-content request-share))))
    
    (is (friend-request-model/requested-at
          (share-model/get-content request-share)))
    (is (= @network-destination test-util/test-destination))
    (is (= @network-data
           { :service service/service-name
             :data { :action request-friendship-action/action
                     :message test-message
                     :profile (select-keys (profile-model/current-user)
                                [profile-model/alias-key
                                 profile-model/identity-key
                                 profile-model/identity-algorithm-key]) }
             :from { :destination test-util/test-destination-str } }))
    (share-model/delete-share request-share))
  (io/delete-file test-util/test-masques-id-file))