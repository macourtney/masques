(ns masques.controller.main.test.main-frame
  (:require [masques.test.util :as test-util])
  (:require [masques.controller.profile.panel :as profile-panel]
            [seesaw.core :as seesaw-core]
            test.init)
  (:use clojure.test
        masques.controller.main.main-frame))

(use-fixtures :once test-util/login-fixture)

(deftest test-show
  (let [frame (show)]
    ;(Thread/sleep 1000)
    (is frame)
    (is (.isShowing frame))
    (show-panel frame profile-panel/panel-name-str)
    ;(Thread/sleep 2000)
    (.setVisible frame false)
    (.dispose frame)
    ;(Thread/sleep 100)
    (is (not (.isShowing frame)))
    ))