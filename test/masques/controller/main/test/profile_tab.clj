(ns masques.controller.main.test.profile-tab
  (:require [fixtures.friend :as friend-fixture]
            [masques.test.util :as test-util]
            [masques.view.main.profile-tab :as profile-tab-view])
  (:use clojure.test
        masques.controller.main.profile-tab))

(test-util/use-combined-login-fixture friend-fixture/fixture-map)

(deftest test-show
  (let [frame (test-util/assert-show (profile-tab-view/create) init)]
    (test-util/assert-close frame)))