(ns masques.controller.identity.test.view
  (:require [fixtures.identity :as fixtures-identity]
            [seesaw.core :as seesaw-core]) 
  (:use clojure.test
        masques.controller.identity.view))

(deftest test-create
  (let [frame (show nil (first fixtures-identity/records))]
    (is frame)
    (is (.isShowing frame))
    (.doClick (seesaw-core/select frame ["#cancel-button"]))
    (is (not (.isShowing frame)))))