(ns masques.view.friend.test.my-requests-table-model
  (:require test.init
            [clj-internationalization.term :as term]
            [clojure.java.io :as io]
            [masques.model.friend-request :as friend-request]
            [masques.model.profile :as profile]
            [masques.model.share :as share]
            [masques.test.util :as test-util])
  (:use clojure.test
        masques.view.friend.my-requests-table-model)
  (:import [javax.swing ImageIcon]))

(deftest test-create
  (let [test-profile (profile/find-profile (profile/save test-util/profile-map)) 
        test-message "test message"
        request-share
          (friend-request/receive-request test-profile test-message)
        test-model (create)]
    (is test-model)

    (is (= (.getColumnClass test-model 0) ImageIcon))
    (is (= (.getColumnClass test-model 1) String))
    (is (= (.getColumnCount test-model) (count columns)))
    (is (= (.getColumnName test-model 1) (term/alias)))

    (is (= (.getRowCount test-model) 1))
    (is (= (.getValueAt test-model 0 1)
           (profile/alias-key test-util/profile-map)))
    (is (= (.getValueAt test-model 0 2) test-message))
    
    (share/delete-share request-share)
    (profile/delete-profile test-profile)))