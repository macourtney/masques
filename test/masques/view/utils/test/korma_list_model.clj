(ns masques.view.utils.test.korma-list-model
  (:require test.init
            [clojure.tools.logging :as logging]
            [masques.model.base :as model-base]
            [masques.view.utils.listener-list :as listener-list])
  (:use clojure.test
        masques.view.utils.korma-list-model))

(def item0 { :id 1 :text "item0" })
(def item1 { :id 0 :text "item1" })

(def items [item0 item1])

(deftype TestListDbModel []
  ListDbModel
  (db-entity [this]
   nil)
  
  (size [this]
    (count items))
  
  (element-at [this index]
     (nth items index))
  
  (index-of [this record-or-id]
    (model-base/index-of record-or-id items)))

(deftest test-create
  (let [korma-list-model (create (new TestListDbModel))
        korma-list-model-listener (list-db-listeners korma-list-model)]
    (is (= (.getSize korma-list-model) (count items)))
    (is (= (.getElementAt korma-list-model 0) item0))
    (is (= (.getElementAt korma-list-model 1) item1))))