(ns masques.view.login.test.create-user
  (:require [seesaw.core :as seesaw-core]
            [config.environments.test :as env])
  (:use clojure.test
        masques.view.login.create-user))

(deftest test-create
  (let [create-user-frame (create nil)]
    (is create-user-frame)
    (seesaw-core/show! create-user-frame)
    (Thread/sleep env/view-sleep-time)
    (seesaw-core/hide! create-user-frame)
    (seesaw-core/dispose! create-user-frame)))