(ns masques.view.group.test.panel
  (:require test.init)
  (:use clojure.test
        masques.view.group.panel)
  (:require [masques.model.base :as model-base]
            [masques.view.utils.korma-combobox-model :as korma-combobox-model]))

(deftest test-create
  (is (empty? (model-base/all-listeners
                model-base/change-listeners model-base/grouping)))
  (let [group-panel (create)]
    (is group-panel)
    (is (= 1 (count (model-base/all-listeners
                      model-base/change-listeners model-base/grouping))))
    (let [group-combobox (find-group-combobox group-panel)]
      (is group-combobox)
      (korma-combobox-model/destroy-model group-combobox)
      (is (empty? (model-base/all-listeners
                    model-base/change-listeners model-base/grouping))))))