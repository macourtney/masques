(ns masques.view.login.test.login
  (:require [seesaw.core :as seesaw-core]
            [config.environments.test :as env])
  (:use clojure.test
        masques.view.login.login))

(deftest test-create
  (let [login-frame (create)]
    (is login-frame)
    (seesaw-core/show! login-frame)
    (Thread/sleep env/view-sleep-time)
    (seesaw-core/hide! login-frame)
    (seesaw-core/dispose! login-frame)))