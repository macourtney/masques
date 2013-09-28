(ns masques.view.login.test.create-user
  (:require [seesaw.core :as seesaw-core])
  (:use clojure.test
        masques.view.login.create-user))

(deftest test-create
  (let [create-user-frame (create nil)]
    (is create-user-frame)
    (seesaw-core/show! create-user-frame)
    ;(Thread/sleep 10000)
    (seesaw-core/hide! create-user-frame)
    (seesaw-core/dispose! create-user-frame)))