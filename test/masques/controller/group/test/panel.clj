(ns masques.controller.group.test.panel
  (:require [test.init :as test-init]
            [masques.model.base :as model-base])
  (:use clojure.test
        masques.controller.group.panel))

(deftest test-create
  (is (not (model-base/insert-interceptors?)))
  (is (create)))