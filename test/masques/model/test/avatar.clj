(ns masques.model.test.avatar
  (:require test.init
            [config.db-config :as db-config])
  (:use clojure.test
        masques.model.avatar))

(deftest test-add-avatar
  (db-config/update-username-password "ted" "secret")
  (create-avatar-image "/Users/ted/ted.png"))