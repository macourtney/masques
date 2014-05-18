(ns masques.view.main.test.main-frame
  (:require [seesaw.core :as seesaw-core]
            [config.environments.test :as env])
  (:use clojure.test
        masques.view.main.main-frame))

(deftest test-create
  (let [main-frame (create)]
    (is main-frame)
    (seesaw-core/show! main-frame)
    (Thread/sleep env/view-sleep-time)
    (seesaw-core/hide! main-frame)
    (destroy main-frame)
    (seesaw-core/dispose! main-frame)))