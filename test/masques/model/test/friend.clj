(ns masques.model.test.friend
  (:require [fixtures.identity :as fixtures-identity]) 
  (:use clojure.test
        masques.model.friend))

(def test-identity (first fixtures-identity/records))
(def test-identity-2 (second fixtures-identity/records))

(def test-friend { :id 1 :identity_id (:id test-identity) :friend_id (:id test-identity-2) })

(deftest test-all-friends
  (let [friend-id (insert test-friend)]
    (is friend-id)
    (try
      (let [test-friends (all-friends test-identity)]
        (is (= [test-friend] test-friends)))
      (finally
        (destroy-record { :id friend-id })))))