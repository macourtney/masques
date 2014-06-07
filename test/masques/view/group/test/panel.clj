(ns masques.view.group.test.panel
  (:require test.init)
  (:use clojure.test
        masques.view.group.panel)
  (:require [masques.model.base :as model-base]
            [masques.view.utils.korma-combobox-model :as korma-combobox-model]))

(deftest test-create
  (let [interceptor-count (count (model-base/all-interceptors
                                   model-base/update-interceptors
                                   model-base/grouping))
        group-panel (create)]
    (is group-panel)
    (is (= (inc interceptor-count) (count (model-base/all-interceptors
                      model-base/update-interceptors model-base/grouping))))
    (let [group-combobox (find-group-combobox group-panel)]
      (is group-combobox)
      (korma-combobox-model/destroy-model group-combobox)
      (is (= interceptor-count
             (count 
               (model-base/all-interceptors
                 model-base/update-interceptors model-base/grouping)))))))