(ns masques.view.login.test.login
  (:require [seesaw.core :as seesaw-core])
  (:use clojure.test
        masques.view.login.login))

(deftest test-create
  (let [login-frame (create)]
    (is login-frame)
    (seesaw-core/show! login-frame)
    ;(Thread/sleep 10000)
    (seesaw-core/hide! login-frame)
    (seesaw-core/dispose! login-frame)))