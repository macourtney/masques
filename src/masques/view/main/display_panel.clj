(ns masques.view.main.display-panel
  (:require [masques.controller.main.panel-protocol :as panel-protocol]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(def id :display-panel)
(def display-card-panel-id :display-card-panel)

(defn create-card-panel []
  (seesaw-core/card-panel :id display-card-panel-id :items []))

(defn create []
  (seesaw-core/border-panel
    :id id
    :center (create-card-panel)))

(defn add-panel [display-panel panel]
  (let [card-panel (view-utils/find-component display-panel display-card-panel-id)
        current-panels (seesaw-core/config card-panel :items)]
    (seesaw-core/config! card-panel
                         :items (cons [(panel-protocol/panel-name panel) (panel-protocol/panel panel)] current-panels))))