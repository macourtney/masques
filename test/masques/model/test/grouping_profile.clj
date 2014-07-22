(ns masques.model.test.grouping-profile
  (:require test.init)
  (:use clojure.test
        masques.model.base
        masques.model.grouping-profile)
  (:require [korma.core :as korma]
            [masques.model.grouping :as grouping]
            [masques.model.profile :as profile]
            [masques.test.util :as test-util]))

(deftest test-crud
  (let [test-grouping (grouping/find-grouping
                        (grouping/save { grouping/display-key "Test Group"
                                         grouping/name-key "test-grouping"
                                         grouping/user-generated-key false }))
        test-profile (profile/find-profile
                       (profile/save test-util/profile-map))
        test-grouping-profile (find-grouping-profile
                                (save
                                  (create-grouping-profile test-grouping
                                                           test-profile)))]
    (is test-grouping-profile)
    (is (find-grouping-profile test-grouping-profile))
    (is (id test-grouping-profile))
    (is (instance? org.joda.time.DateTime
                   (created-at-key test-grouping-profile)))
    (is (= (grouping-id-key test-grouping-profile) (id test-grouping)))
    (is (= (profile-id-key test-grouping-profile) (id test-profile)))
    
    (is (= (find-group test-grouping-profile) test-grouping))
    (is (= (find-profile test-grouping-profile) test-profile))
    
    (is (= (count-grouping-profiles test-grouping) 1))
    (is (= (count-grouping-profiles { id-key -1 }) 0))
    (is (= (count-grouping-profiles nil) 0))
    (is (= (id (find-table-grouping-profile test-grouping 0))
           (id test-grouping-profile)))
    (is (nil? (find-table-grouping-profile test-grouping 1)))
    (is (nil? (find-table-grouping-profile test-grouping nil)))
    (is (nil? (find-table-grouping-profile { id-key -1 } 0)))
    (is (nil? (find-table-grouping-profile nil 0)))
    (is (nil? (find-table-grouping-profile nil nil)))
    (is (= (table-index-of test-grouping-profile) 0))
    (is (nil? (table-index-of { id-key -1 })))
    (is (nil? (table-index-of nil)))
    
    (delete-grouping-profiles test-profile)
    (is (nil? (find-grouping-profile (id test-grouping-profile))))
    
    (delete-grouping-profile test-grouping-profile)
    (grouping/delete-grouping test-grouping)
    (profile/delete-profile test-profile)))