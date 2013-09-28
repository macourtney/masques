(ns masques.model.test.profile
  (:require test.init)
  (:use clojure.test
        masques.model.profile))

(def profile-record (save {
  :alias "Fred"
  ; :avatar-path "/Users/Ted/masques/avatar.png"
}))

(deftest test-add-profile
  (save profile-record)
  (is profile-record))
  ; (is (:id share-record))
  ; (is (= (:content-type share-record) "message"))
  ; (is (:message-id share-record))
  ; (is (not (nil? (:uuid share-record))))
  ; (is (instance? org.joda.time.DateTime (:created-at share-record))))

(deftest create-user-profile
  (let [user-profile (create-user "Ted")]
    (is user-profile)
    (= (:alias user-profile) "Ted")
    (println user-profile)))

