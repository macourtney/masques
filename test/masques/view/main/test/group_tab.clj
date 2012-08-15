(ns masques.view.main.test.group-tab
  (:use clojure.test
        masques.view.main.group-tab)
  (:require [masques.test.util :as test-util]))

(deftest test-create
  (let [group-tab (create)]
    (is group-tab)))