(ns masques.controller.main.test.main-frame
  (:require [fixtures.peer :as peer-fixture]
            [fixtures.util :as fixtures-util]
            [masques.model.peer :as peer-model]
            [seesaw.core :as seesaw-core])
  (:use clojure.test
        masques.controller.main.main-frame))

(fixtures-util/use-fixture-maps :once peer-fixture/fixture-map)

(deftest test-show
  (is (= (peer-model/peer-update-listener-count) 0)) 
  (is (= (peer-model/peer-delete-listener-count) 0))
  (let [frame (show)]
    (Thread/sleep 100)
    (is frame)
    (is (.isShowing frame))
    (is (= (peer-model/peer-update-listener-count) 1)) 
    (is (= (peer-model/peer-delete-listener-count) 1))
    (.setVisible frame false)
    (.dispose frame)
    (Thread/sleep 100)
    (is (not (.isShowing frame)))
    (is (= (peer-model/peer-update-listener-count) 0)) 
    (is (= (peer-model/peer-delete-listener-count) 0))))