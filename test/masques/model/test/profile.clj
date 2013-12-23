(ns masques.model.test.profile
  (:require test.init
            [config.db-config :as db-config]
            [korma.core :as korma]
            [masques.model.base :as model-base])
  (:use clojure.test
        masques.model.profile))

(def profile-map {
  :alias "Ted"
  :avatar-path "/Users/ted/ted.png"
})

(deftest test-add-profile
  (let [profile-record (save profile-map)]
    (is profile-record)
    (is (:id profile-record))
    (is (= (:alias profile-record) "Ted"))
    (is (instance? org.joda.time.DateTime (:created-at profile-record)))))

(deftest test-build-profile
  (let [built-profile (build (:id (save profile-map)))]
    ;(println "\n\nBUILT PROFILE\n\n" built-profile)
    (is built-profile)
    (is (map? (:avatar built-profile)))
    (is (= (:id (:avatar built-profile)) (:avatar-file-id built-profile)))
    (is (= (:path (:avatar built-profile)) (:avatar-path profile-map)))))

(deftest test-create-user-profile
  (let [user-profile (create-user "Ted")]
    (is user-profile)
    (is (= (:alias user-profile) "Ted"))))

(deftest test-init
  (is (nil? (current-user)))
  (init)
  (is (= (current-user) (find-logged-in-user (db-config/current-username))))
  (logout)
  (is (nil? (current-user))))
