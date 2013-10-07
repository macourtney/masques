(ns masques.controller.login.test.login
  (:require [fixtures.identity :as fixtures-identity]
            [seesaw.core :as seesaw-core]) 
  (:use clojure.test
        masques.controller.login.login))

(deftest test-show
  (let [frame (show)]
    (is frame)
    (is (.isShowing frame))
    (.setVisible frame false)
    (.dispose frame)
    (is (not (.isShowing frame)))))