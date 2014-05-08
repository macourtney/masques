(ns masques.model.test.friend-request
  (:require test.init
            [clj-time.core :as clj-time]
            [clojure.java.io :as io]
            [masques.model.message :as message]
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

(deftest test-update-send-request
  (let [test-message "test message"
        test-profile (profile/load-masques-id-map test-util/profile-map)
        test-request (save { created-at-key (str (clj-time/now))
                             request-status-key approved-status
                             requested-at-key (str (clj-time/now))
                             request-approved-at-key (str (clj-time/now))
                             profile-id-key (id test-profile) })
        test-share (share/create-send-friend-request-share
                     test-message test-profile test-request)]
    (let [test-message-2 "test message 2"
          new-status (status test-request approved-status)
          updated-share (update-send-request test-message-2 test-profile
                           (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share))))
    (let [test-message-3 "test message 3"
          new-status (status test-request pending-received-status)
          updated-share (update-send-request test-message-3 test-profile
                           (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share)))
      (is (= (message/body (share/message-id updated-share))
             test-message-3))
      (is (= (status updated-request) approved-status)))
    (let [test-message-4 "test message 4"
          new-status (status test-request pending-sent-status)
          updated-share (update-send-request test-message-4 test-profile
                          (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share)))
      (is (= (message/body (share/message-id updated-share))
             test-message-4))
      (is (= (status updated-request) pending-sent-status)))
    (let [test-message-5 "test message 5"
          new-status (status test-request rejected-status)
          updated-share (update-send-request test-message-5 test-profile
                          (find-friend-request test-request))]
      (is (not updated-share)))
    (let [test-message-6 "test message 6"
          new-status (status test-request unfriend-status)
          updated-share (update-send-request test-message-6 test-profile
                          (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share)))
      (is (= (message/body (share/message-id updated-share))
             test-message-6))
      (is (= (status updated-request) pending-sent-status)))
    (share/delete-share test-share)
    (profile/delete-profile test-profile)))

(deftest test-update-receive-request
  (let [test-message "test message"
        test-profile (profile/load-masques-id-map test-util/profile-map)
        test-request (save { created-at-key (str (clj-time/now))
                             request-status-key approved-status
                             requested-at-key (str (clj-time/now))
                             request-approved-at-key (str (clj-time/now))
                             profile-id-key (id test-profile) })
        test-share (share/create-received-friend-request-share
                     test-message test-profile test-request)]
    (let [test-message-2 "test message 2"
          new-status (status test-request approved-status)
          updated-share (update-receive-request test-message-2 test-profile
                           (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share))))
    (let [test-message-3 "test message 3"
          new-status (status test-request pending-received-status)
          updated-share (update-receive-request test-message-3 test-profile
                           (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share)))
      (is (= (message/body (share/message-id updated-share))
             test-message-3))
      (is (= (status updated-request) pending-received-status)))
    (let [test-message-4 "test message 4"
          new-status (status test-request pending-sent-status)
          updated-share (update-receive-request test-message-4 test-profile
                          (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share)))
      (is (= (message/body (share/message-id updated-share))
             test-message-4))
      (is (= (status updated-request) approved-status)))
    (let [test-message-5 "test message 5"
          new-status (status test-request rejected-status)
          updated-share (update-receive-request test-message-5 test-profile
                          (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share)))
      (is (= (message/body (share/message-id updated-share))
             test-message-5))
      (is (= (status updated-request) approved-status)))
    (let [test-message-6 "test message 6"
          new-status (status test-request unfriend-status)
          updated-share (update-receive-request test-message-6 test-profile
                          (find-friend-request test-request))]
      (is (not updated-share)))
    (share/delete-share test-share)
    (profile/delete-profile test-profile)))

(deftest test-send-request
  (is (= 0 (count-pending-sent-requests)))
  (profile/create-masques-id-file test-util/test-masques-id-file
                                  test-util/profile-map)
  (let [friend-request-share (send-request test-util/test-masques-id-file
                                           "test message")
        friend-request (share/get-content friend-request-share)]
    (is friend-request)
    (is (= 1 (count-pending-sent-requests)))
    (is (= (select-keys friend-request [:id :profile-id])
           (pending-sent-request 0)))
    (is (= (request-status-key friend-request) pending-sent-status))
    (is (not (requested-at-key friend-request)))
    (is (not (request-approved-at-key friend-request)))
    (let [profile-id (profile-id-key friend-request)]
      (is profile-id)
      (is (profile/find-profile profile-id))
      (let [to-profile (find-to-profile friend-request)]
        (is to-profile)
        (is (= (id to-profile) profile-id))))
    (delete-friend-request friend-request)
    (share/delete-share friend-request-share))
  (io/delete-file test-util/test-masques-id-file))

(deftest test-receive-request
  (is (= 0 (count-pending-received-requests)))
  (let [test-message "test message"
        friend-request-share (receive-request
                               test-util/profile-map test-message)
        friend-request (share/get-content friend-request-share)]
    (is friend-request)
    (is (= 1 (count-pending-received-requests)))
    (is (= (select-keys friend-request [:id :profile-id])
           (pending-received-request 0)))
    (is (= (request-status-key friend-request) pending-received-status))
    (is (requested-at-key friend-request))
    (is (not (request-approved-at-key friend-request)))
    (let [profile-id (profile-id-key friend-request)]
      (is profile-id)
      (is (profile/find-profile profile-id))
      (let [to-profile (find-to-profile friend-request)]
        (is to-profile)
        (is (= (id to-profile) profile-id))
        (profile/delete-profile to-profile)))
    (delete-friend-request friend-request)
    (share/delete-share friend-request-share)))

(deftest test-unfriend
  (let [test-message "test message"
        test-profile (profile/load-masques-id-map test-util/profile-map)
        test-request (save { created-at-key (str (clj-time/now))
                             request-status-key approved-status
                             requested-at-key (str (clj-time/now))
                             request-approved-at-key (str (clj-time/now))
                             profile-id-key (id test-profile) })
        test-share (share/create-received-friend-request-share
                     test-message test-profile test-request)]
    (let [new-status (status test-request approved-status)
          updated-share (unfriend (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share)))
      (is (= (status updated-request) unfriend-status)))
    (let [new-status (status test-request pending-received-status)
          updated-share (unfriend (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share)))
      (is (= (status updated-request) unfriend-status)))
    (let [new-status (status test-request rejected-status)
          updated-share (unfriend (find-friend-request test-request))]
      (is (not updated-share)))
    (let [new-status (status test-request unfriend-status)
          updated-share (unfriend (find-friend-request test-request))]
      (is (not updated-share)))
    (let [new-status (status test-request pending-sent-status)
          updated-share (unfriend (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is (not updated-share))
      (is (not (find-friend-request test-request))))
    (share/delete-share test-share)
    (profile/delete-profile test-profile)))

(deftest test-reject
  (let [test-message "test message"
        test-profile (profile/load-masques-id-map test-util/profile-map)
        test-request (save { created-at-key (str (clj-time/now))
                             request-status-key approved-status
                             requested-at-key (str (clj-time/now))
                             request-approved-at-key (str (clj-time/now))
                             profile-id-key (id test-profile) })
        test-share (share/create-send-friend-request-share
                     test-message test-profile test-request)]
    (let [new-status (status test-request approved-status)
          updated-share (reject (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share)))
      (is (= (status updated-request) rejected-status)))
    (let [new-status (status test-request pending-sent-status)
          updated-share (reject (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is updated-request)
      (is (= (id updated-share) (id test-share)))
      (is (= (status updated-request) rejected-status)))
    (let [new-status (status test-request rejected-status)
          updated-share (reject (find-friend-request test-request))]
      (is (not updated-share)))
    (let [new-status (status test-request unfriend-status)
          updated-share (reject (find-friend-request test-request))]
      (is (not updated-share)))
    (let [new-status (status test-request pending-received-status)
          updated-share (reject (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is (not updated-share))
      (is (not (find-friend-request test-request))))
    (share/delete-share test-share)
    (profile/delete-profile test-profile)))

(deftest test-send-accept
  (let [test-message "test message"
        test-profile (profile/load-masques-id-map test-util/profile-map)
        test-request (save { created-at-key (str (clj-time/now))
                             request-status-key approved-status
                             requested-at-key (str (clj-time/now))
                             request-approved-at-key (str (clj-time/now))
                             profile-id-key (id test-profile) })
        test-share (share/create-send-friend-request-share
                     test-message test-profile test-request)]
    (let [new-status (status test-request approved-status)
          updated-share (send-accept (find-friend-request test-request))]
      (is (not updated-share)))
    (let [new-status (status test-request pending-received-status)
          updated-share (send-accept (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share)))
      (is (= (status updated-request) approved-status)))
    (let [new-status (status test-request pending-sent-status)
          updated-share (send-accept (find-friend-request test-request))]
      (is (not updated-share)))
    (let [new-status (status test-request rejected-status)
          updated-share (send-accept (find-friend-request test-request))]
      (is (not updated-share)))
    (let [new-status (status test-request unfriend-status)
          updated-share (send-accept (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share)))
      (is (= (status updated-request) approved-status)))
    (share/delete-share test-share)
    (profile/delete-profile test-profile)))

(deftest test-receive-accept
  (let [test-message "test message"
        test-profile (profile/load-masques-id-map test-util/profile-map)
        test-request (save { created-at-key (str (clj-time/now))
                             request-status-key approved-status
                             requested-at-key (str (clj-time/now))
                             request-approved-at-key (str (clj-time/now))
                             profile-id-key (id test-profile) })
        test-share (share/create-send-friend-request-share
                     test-message test-profile test-request)]
    (let [new-status (status test-request approved-status)
          updated-share (receive-accept (find-friend-request test-request))]
      (is (not updated-share)))
    (let [new-status (status test-request pending-received-status)
          updated-share (receive-accept (find-friend-request test-request))]
      (is (not updated-share)))
    (let [new-status (status test-request pending-sent-status)
          updated-share (receive-accept (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share)))
      (is (= (status updated-request) approved-status)))
    (let [new-status (status test-request rejected-status)
          updated-share (receive-accept (find-friend-request test-request))
          updated-request (share/get-content updated-share)]
      (is updated-share)
      (is (= (id updated-share) (id test-share)))
      (is (= (status updated-request) approved-status)))
    (let [new-status (status test-request unfriend-status)
          updated-share (receive-accept (find-friend-request test-request))]
      (is (not updated-share)))
    (share/delete-share test-share)
    (profile/delete-profile test-profile)))