(ns masques.controller.group.add
  (:require [clojure.java.io :as java-io]
            [masques.controller.actions.utils :as actions-utils]
            [masques.controller.utils :as controller-utils]
            ;[masques.model.group :as group-model]
            [masques.view.group.add :as add-group-view]
            [seesaw.core :as seesaw-core])
  (:import [java.io StringWriter]))

(defn attach-cancel-action [add-group-panel]
  (actions-utils/attach-window-close-listener add-group-panel "#cancel-button"))

(defn add-action [add-group-panel e]
  (controller-utils/disable-widget add-group-panel)
  (if-let [group-text-str (add-group-view/group-text add-group-panel)]
    (do
      ;(group-model/add-group group-text-str)
      (actions-utils/close-window add-group-panel))
    (controller-utils/enable-widget add-group-panel)))

(defn attach-add-action [add-group-panel]
  (actions-utils/attach-frame-listener add-group-panel "#add-button" add-action))

(defn attach [add-group-panel]
  (attach-add-action (attach-cancel-action add-group-panel)))

(defn show [parent-component]
  (controller-utils/show (attach (add-group-view/create parent-component))))