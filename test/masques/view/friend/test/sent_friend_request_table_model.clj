(ns masques.view.friend.test.sent-friend-request-table-model
  (:require test.init
            [clj-internationalization.term :as term]
            [clojure.java.io :as io]
            [clojure.tools.logging :as logging]
            [masques.model.friend-request :as friend-request]
            [masques.model.profile :as profile]
            [masques.model.share :as share]
            [masques.test.util :as test-util])
  (:use clojure.test
        masques.view.friend.sent-friend-request-table-model))

(deftest test-create
  (profile/create-masque-file test-util/test-masque-file test-util/profile-map)
  (let [test-message "test message"
        request-share
          (friend-request/send-request test-util/test-masque-file test-message)
        test-model (create)]
    (is test-model)

    (is (= (.getColumnClass test-model 0) Object))
    (is (= (.getColumnClass test-model 1) String))
    (is (= (.getColumnCount test-model) (count columns)))
    (is (= (.getColumnName test-model 1) (term/alias)))

    (is (= (.getRowCount test-model) 1))
    (is (= (.getValueAt test-model 0 1) (profile/alias-key test-util/profile-map)))
    (is (= (.getValueAt test-model 0 2) test-message))
    
    (share/delete-share request-share))
  (io/delete-file test-util/test-masque-file))