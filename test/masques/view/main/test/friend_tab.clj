(ns masques.view.main.test.friend-tab
  (:use clojure.test
        masques.view.main.friend-tab)
  (:require [masques.test.util :as test-util]))

(deftest test-create
  (let [friend-tab (create)]
    (is friend-tab)))