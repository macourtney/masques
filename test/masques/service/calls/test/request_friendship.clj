(ns masques.service.calls.test.request-friendship
  (:require test.init)
  (:use [masques.service.calls.request-friendship])
  (:use [clojure.test])
  (:require [clj-i2p.core :as clj-i2p]
            [clojure.java.io :as io]
            [clojure.tools.logging :as logging]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.service.actions.request-friendship
              :as request-friendship-action]
            [masques.service.protocol :as service-protocol]
            [masques.test.util :as test-util]))

(def network-destination (atom nil))
(def network-data (atom nil))

(def test-message "test message" )

(defn save-mock-network [destination data]
  (reset! network-destination destination)
  (reset! network-data data)
  { clj-i2p/data-key { request-friendship-action/received-key true } })

(test-util/use-combined-login-fixture
  (test-util/create-mock-network-fixture save-mock-network))

(deftest request-friendship-test
  (is (nil? @network-destination))
  (is (nil? @network-data))
  (profile-model/create-masques-id-file test-util/test-masques-id-file
                                        test-util/profile-map)
  (let [test-message "test message"
        request-share (send-friend-request test-util/test-masques-id-file
                                           test-message)]
    (is (friend-request-model/requested-at
          (share-model/get-content request-share)))
    (is (= @network-destination test-util/test-destination))
    (is (= @network-data
           { clj-i2p/service-key service-protocol/service-name
             clj-i2p/service-version-key service-protocol/service-version
             clj-i2p/data-key { :action request-friendship-action/action
                     :message test-message
                     :profile (select-keys (profile-model/current-user)
                                [profile-model/alias-key
                                 profile-model/identity-key
                                 profile-model/identity-algorithm-key]) }
             clj-i2p/from-key
             { clj-i2p/destination-key test-util/test-destination-str } }))
    (share-model/delete-share request-share))
  (io/delete-file test-util/test-masques-id-file))