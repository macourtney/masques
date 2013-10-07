(ns masques.view.main.test.main-frame
  (:require [seesaw.core :as seesaw-core])
  (:use clojure.test
        masques.view.main.main-frame))

(deftest test-create
  (let [main-frame (create)]
    (is main-frame)
    (seesaw-core/show! main-frame)
    ;(Thread/sleep 10000)
    (seesaw-core/hide! main-frame)
    (seesaw-core/dispose! main-frame)))