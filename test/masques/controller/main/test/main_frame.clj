(ns masques.controller.main.test.main-frame
  (:require test.init)
  (:require [fixtures.profile :as profile-fixture]
            [fixtures.util :as fixtures-util]
            [masques.controller.profile.panel :as profile-panel]
            [masques.model.base :as model-base]
            [masques.model.profile :as profile-model]
            [masques.test.util :as test-util]
            [seesaw.core :as seesaw-core])
  (:use clojure.test
        masques.controller.main.main-frame))

(use-fixtures :once (join-fixtures [(fixtures-util/create-fixture [profile-fixture/fixture-map]) test-util/login-fixture]))

(deftest test-show
  (profile-model/save (assoc (profile-model/current-user) :page "Test page for the logged in user."))
  (profile-model/reload-current-user)
  (is (not (model-base/insert-interceptors?)))
  (let [frame (show)]
    (is frame)
    (is (.isShowing frame))
    (show-panel frame profile-panel/panel-name-str)
    (is (model-base/insert-interceptors?))
    (.setVisible frame false)
    (destroy frame)
    (.dispose frame)
    (is (not (.isShowing frame)))
    (is (not (model-base/insert-interceptors?)))))