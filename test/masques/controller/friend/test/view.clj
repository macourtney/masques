(ns masques.controller.friend.test.view
  (:require [test.init :as test-init])
  (:require [fixtures.identity :as identity-fixture]
            [clojure.java.io :as java-io]
            [masques.model.clipboard :as clipboard-model]
            [masques.model.friend :as friend-model]
            [masques.test.util :as test-util]
            [masques.view.friend.add :as add-friend-view]
            [seesaw.core :as seesaw-core])
  (:use clojure.test
        masques.controller.friend.view)
  (:import [java.io File]))

(test-util/use-combined-login-fixture identity-fixture/fixture-map)

(deftest test-create 
  (let [frame (show nil)]
    (is frame)
    (is (.isShowing frame))
    (.setVisible frame false)
    (.dispose frame)
    (is (not (.isShowing frame)))))
