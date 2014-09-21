(ns masques.service.calls.test.send-status
  (:require test.init)
  (:use masques.service.calls.send-status
        clojure.test)
  (:require [clj-i2p.core :as clj-i2p]
            [clojure.java.io :as io]
            [clojure.tools.logging :as logging]
            [fixtures.profile :as profile-fixture]
            [masques.model.base :as base-model]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.grouping :as grouping]
            [masques.model.grouping-profile :as grouping-profile]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.service.actions.update-status :as update-status-action]
            [masques.service.protocol :as service-protocol]
            [masques.test.util :as test-util]))

(def network-destination (atom nil))
(def network-data (atom nil))

(def test-message "test message" )

(def test-profile (second profile-fixture/records))

(defn save-mock-network [destination data]
  (reset! network-destination destination)
  (reset! network-data data)
  { clj-i2p/data-key { :received? true } })

(defn load-profile-fixture [function]
  (grouping/init)
  (let [profile-id (:id test-profile)
        grouping-profile-id (grouping-profile/save
                              (grouping-profile/create-grouping-profile
                                (grouping/find-friends-id) profile-id))]
    (try
      (function)
      (finally
        (grouping-profile/delete-grouping-profile grouping-profile-id)))))

(test-util/use-combined-login-fixture
  load-profile-fixture
  (test-util/create-mock-network-fixture save-mock-network))

(deftest send-status-test
  (is (nil? @network-destination))
  (is (nil? @network-data))
  (try
    (let [test-message "test message"
          request-share (send-status test-message nil [test-profile])]
      (is request-share)
      (is (= @network-destination test-util/test-destination))
      (is (= @network-data
             { clj-i2p/from-key
              { clj-i2p/destination-key test-util/test-destination-str }
              clj-i2p/service-key service-protocol/service-name
              clj-i2p/service-version-key service-protocol/service-version
              :action update-status-action/action
              clj-i2p/data-key { 
                                :message test-message
                                :uuid (base-model/uuid request-share)
                                :profile (select-keys
                                           (profile-model/current-user)
                                           [profile-model/identity-key
                                            profile-model/identity-algorithm-key]) } }))
      (share-model/delete-share request-share))
    (finally
      (reset! network-destination nil)
      (reset! network-data nil))))

(deftest send-status-to-group-test
  (is (nil? @network-destination))
  (is (nil? @network-data))
  
  (try
    (let [test-message "test message"
          request-share (send-status test-message [(grouping/find-friends-id)]
                                     nil)]
      (is request-share)
      (is (= @network-destination test-util/test-destination))
      (is (= @network-data
             { clj-i2p/from-key
              { clj-i2p/destination-key test-util/test-destination-str }
              clj-i2p/service-key service-protocol/service-name
              clj-i2p/service-version-key service-protocol/service-version
              :action update-status-action/action
              clj-i2p/data-key { 
                                :message test-message
                                :uuid (base-model/uuid request-share)
                                :profile (select-keys
                                           (profile-model/current-user)
                                           [profile-model/identity-key
                                            profile-model/identity-algorithm-key]) } }))
      (share-model/delete-share request-share))
    (finally
      (reset! network-destination nil)
      (reset! network-data nil))))