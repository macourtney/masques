(ns masques.model.test.group-membership
  (:require [fixtures.friend :as fixtures-friend]
            [fixtures.group :as fixtures-group]
            [fixtures.util :as fixtures-util]) 
  (:use clojure.test
        masques.model.group-membership))

(def test-group (first fixtures-group/records))

(def test-friend (first fixtures-friend/records))

(fixtures-util/use-fixture-maps :once [fixtures-friend/fixture-map fixtures-group/fixture-map])

(deftest test-add-and-remove-friend-from-group
  (let [group-membership-id (add-friend-to-group (:id test-friend) (:id test-group))]
    (is group-membership-id)
    (try
      (let [test-group-membership (get-record group-membership-id)]
        (is test-group-membership)
        (is (:group_id test-group-membership) (:id test-group))
        (is (:friend_id test-group-membership) (:id test-friend)))
      (is (group-member? (:id test-friend) (:id test-group)))
      (remove-friend-from-group (:id test-friend) (:id test-group))
      (is (not (group-member? (:id test-friend) (:id test-group))))
      (finally
        (destroy-record { :id group-membership-id })))))