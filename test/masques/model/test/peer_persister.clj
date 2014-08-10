(ns masques.model.test.peer-persister
  (:require test.init
            [clojure.tools.logging :as logging]
            [clj-i2p.peer-service.persister-protocol :as persister-protocol]
            [clj-time.core :as clj-time]
            [masques.model.friend-request :as friend-request]
            [masques.model.profile :as profile]
            [masques.test.util :as test-util])
  (:use clojure.test
        masques.model.base
        masques.model.peer-persister))

(deftest test-online-peers
  (is (empty? @online-peers))
  (let [test-profile (profile/find-profile
                       (profile/load-masque-map test-util/profile-map))]
    (try
      (set-online test-profile)
      (is (= (count @online-peers) 1))
      (is (= (id (find-online-peer test-profile)) (id test-profile)))
      (set-offline test-profile)
      (is (empty? @online-peers))
      (finally
        (profile/delete-profile test-profile)))))

(deftest test-update-peer
  (is (empty? @online-peers))
  (let [test-profile (profile/find-profile
                       (profile/load-masque-map test-util/profile-map))
        peer-persister (create-peer-persister)]
    (try
      (persister-protocol/update-peer
        peer-persister { :id (id test-profile) :notified true })
      (is (= (count @online-peers) 1))
      (is (= (id (find-online-peer test-profile)) (id test-profile)))
      (set-offline test-profile)
      (is (empty? @online-peers))
      (finally
        (profile/delete-profile test-profile)))))

(deftest test-send-message-fail-listener
  (is (empty? @online-peers))
  (let [test-profile (profile/find-profile
                       (profile/load-masque-map test-util/profile-map))]
    (try
      (set-online test-profile)
      (is (= (count @online-peers) 1))
      (is (= (id (find-online-peer test-profile)) (id test-profile)))
      (send-message-fail-listener (profile/destination test-profile) nil)
      (is (empty? @online-peers))
      (finally
        (profile/delete-profile test-profile)))))

(deftest test-find-peer
  (let [peer-persister (create-peer-persister)]
    (let [test-profile (profile/load-masque-map test-util/profile-map)
          test-request (friend-request/find-friend-request
                         (friend-request/save
                           { friend-request/created-at-key (str (clj-time/now))
                            friend-request/request-status-key
                              friend-request/approved-status
                            friend-request/requested-at-key (str (clj-time/now))
                            friend-request/request-approved-at-key
                              (str (clj-time/now))
                            friend-request/profile-id-key (id test-profile) }))]
      (try
        (is (= test-profile
               (id
                 (persister-protocol/find-peer
                   peer-persister
                   { :destination test-util/test-destination-str }))))
        (finally
          (friend-request/delete-friend-request test-request)
          (profile/delete-profile test-profile))))
    (let [test-profile (profile/load-masque-map test-util/profile-map)
          test-request (friend-request/find-friend-request
                         (friend-request/save
                           { friend-request/created-at-key (str (clj-time/now))
                            friend-request/request-status-key
                              friend-request/rejected-status
                            friend-request/requested-at-key (str (clj-time/now))
                            friend-request/request-approved-at-key
                              (str (clj-time/now))
                            friend-request/profile-id-key (id test-profile) }))]
      (try
        (is (nil? (persister-protocol/find-peer
                    peer-persister
                    { :destination test-util/test-destination-str })))
        (finally
          (friend-request/delete-friend-request test-request)
          (profile/delete-profile test-profile))))))