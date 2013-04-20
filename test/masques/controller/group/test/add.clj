(ns masques.controller.group.test.add
  (:require [test.init :as test-init])
  (:require [fixtures.identity :as identity-fixture]
            [clojure.java.io :as java-io]
            [masques.model.group :as group-model]
            [masques.test.util :as test-util]
            [masques.view.group.add :as add-group-view]
            [seesaw.core :as seesaw-core])
  (:use clojure.test
        masques.controller.group.add)
  (:import [java.io File]))

;(test-util/use-combined-login-fixture identity-fixture/fixture-map)

(def test-group-name "test-group")

;(deftest test-create
;  (is (= (count (group-model/find-identity-groups)) 0)) 
;  (let [frame (show nil)]
;    (is frame)
;    (is (.isShowing frame))
;    (add-group-view/click-add-button frame)
;    (is (.isShowing frame))
;    (is (= (count (group-model/find-identity-groups)) 0))
;    (add-group-view/group-text frame test-group-name)
;    (add-group-view/click-add-button frame)
;    (let [all-groups (group-model/find-identity-groups)]
;      (is (= (count all-groups) 1))
;      (doseq [group all-groups]
;        (group-model/destroy-record group)))
;    (is (not (.isShowing frame)))))

;(deftest test-cancel
;  (is (= (count (group-model/find-identity-groups)) 0))
;  (let [frame (show nil)]
;    (is frame)
;    (is (.isShowing frame))
;    (add-group-view/click-cancel-button frame)
;    (is (= (count (group-model/find-identity-groups)) 0))
;    (is (not (.isShowing frame)))))