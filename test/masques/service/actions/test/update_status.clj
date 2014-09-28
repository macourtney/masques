(ns masques.service.actions.test.update-status
  (:require test.init)
  (:use masques.service.actions.update-status
        clojure.test)
  (:require [clojure.tools.logging :as logging]
            [masques.model.base :as base-model]
            [masques.model.message :as message-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.model.share-profile :as share-profile-model]
            [masques.test.util :as test-util]))

(def test-message "test-message")
(def test-profile
  (select-keys test-util/profile-map 
    [profile-model/alias-key profile-model/identity-key
     profile-model/identity-algorithm-key]))
(def test-uuid (base-model/uuid))

(def test-request-map { :from { :destination test-util/test-destination-str }
                        :data { :message test-message
                                :profile test-profile
                                :uuid test-uuid } })

(test-util/use-combined-login-fixture)

(deftest run-test
  (let [response (run test-request-map)]
    (is response)
    (is (true? (:received? (:data response))))
    (let [update-status-share
            (share-model/find-share { share-model/uuid-key test-uuid })]
      (logging/debug "update-status-share:" update-status-share)
      (is update-status-share)
      (is (= (share-model/get-type update-status-share)
             share-model/status-type))
      (is (= (share-model/from-profile update-status-share)
             (profile-model/find-by-identity test-util/profile-map)))
      (is (= (message-model/body (share-model/message-id update-status-share))
             test-message))
      (let [share-profile-ids (share-profile-model/ids-for-share
                                update-status-share)]
        (is (= (count share-profile-ids) 1))
        (let [share-profile-record (share-profile-model/find-share-profile
                                     (first share-profile-ids))]
          (is share-profile-record)
          (is (= (share-profile-model/profile-to-id-key share-profile-record)
                 (base-model/id (profile-model/current-user))))
          (is (= (share-profile-model/share-id-key share-profile-record)
                 (base-model/id update-status-share)))
          (is (nil? (share-profile-model/shown-in-stream-at-key
                      share-profile-record)))
          (is (share-profile-model/transferred-at-key share-profile-record))))
      (share-model/delete-share update-status-share))))