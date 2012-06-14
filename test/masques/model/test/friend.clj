(ns masques.model.test.friend
  (:require [fixtures.identity :as fixtures-identity]) 
  (:use clojure.test
        masques.model.friend))

(def test-identity (first fixtures-identity/records))
(def test-identity-2 (second fixtures-identity/records))

(def test-friend { :identity_id (:id test-identity) :friend_id (:id test-identity-2) })

(deftest test-all-friends
  (let [friend-id (insert test-friend)]
    (is friend-id)
    (try
      (let [test-friends (all-friends test-identity)]
        (is (= [(assoc test-friend :id friend-id)] test-friends)))
      (finally
        (when friend-id
          (destroy-record { :id friend-id }))))))

(deftest test-add-friend
  (is (nil? (add-friend test-identity nil)))
  (is (nil? (add-friend nil test-identity-2)))
  (is (nil? (add-friend nil nil)))
  (is (empty? (all-friends test-identity)))
  (let [friend-id (add-friend test-identity-2 test-identity)]
    (is friend-id)
    (try
      (is (friend? test-identity-2 test-identity))
      (let [test-friends (all-friends test-identity)]
        (is (= [(assoc test-friend :id friend-id)] test-friends)))
      (finally
        (when friend-id
          (remove-friend test-identity-2 test-identity)
          (is (not (find-record { :id friend-id }))))))))