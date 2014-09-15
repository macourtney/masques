(ns masques.model.test.share-profile
  (:require test.init)
  (:require [masques.model.profile :as profile-model]
            [masques.model.share :as share-model])
  (:use clojure.test
        masques.model.base
        masques.model.share-profile))

(deftest test-create-share-profile
  (let [test-share (share-model/create-share { :content-type "message" })
        test-profile (profile-model/save { profile-model/alias-key "Ted" })
        test-share-profile (create-share-profile test-share test-profile)]
    (is test-share-profile)
    (is (find-share-profile test-share-profile))
    (is (= (ids-for-share test-share) [(id test-share-profile)]))
    (is (= (share-ids-for-profile test-profile) [(id test-share)]))
    (is (= (first-profile-id-for-share test-share) (id test-profile)))
    (delete-all test-share)
    (is (nil? (find-share-profile test-share-profile)))
    (delete-share-profile test-share-profile)
    (profile-model/delete-profile test-profile)
    (share-model/delete-share test-share)))