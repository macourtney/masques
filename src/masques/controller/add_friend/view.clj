(ns masques.controller.add-friend.view
  (:require [masques.controller.actions.utils :as actions-utils]
            [masques.controller.utils :as controller-utils]
            [masques.model.friend :as friend-model]
            [masques.view.add-friend.view :as add-friend-view]
            [seesaw.core :as seesaw-core]))

(defn find-friend-text [add-friend-panel]
  (seesaw-core/select add-friend-panel ["#friend-text"]))

(defn attach-cancel-action [add-friend-panel]
  (actions-utils/attach-window-close-listener add-friend-panel "#cancel-button"))

(defn friend-text
  ([add-friend-panel]
    (.getText (find-friend-text add-friend-panel)))
  ([add-friend-panel text]
    (.setText (find-friend-text add-friend-panel) text)))

(defn add-action [add-friend-panel e]
  (controller-utils/disable-widget add-friend-panel)
  (let [friend-text-str (friend-text add-friend-panel)]
    (if (not-empty friend-text-str)
      (do
        (friend-model/read-friend-xml-string friend-text-str)
        (actions-utils/close-window add-friend-panel))
      (controller-utils/enable-widget add-friend-panel))))

(defn attach-add-action [add-friend-panel]
  (actions-utils/attach-frame-listener add-friend-panel "#add-button" add-action))

(defn attach [add-friend-panel]
  (attach-add-action (attach-cancel-action add-friend-panel)))

(defn show [main-frame]
  (controller-utils/show (attach (add-friend-view/create main-frame))))