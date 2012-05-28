(ns masques.controller.add-destination.add-destination
  (:require [clj-i2p.peer-service.peer :as peer-service]
            [masques.controller.actions.utils :as actions-utils]
            [masques.controller.utils :as controller-utils]
            [masques.view.add-destination.add-destination :as add-destination-view]
            [seesaw.core :as seesaw-core]))

(defn find-destination-text [add-destination-frame]
  (seesaw-core/select add-destination-frame ["#destination-text"]))

(defn attach-cancel-action [add-destination-frame]
  (actions-utils/attach-window-close-listener add-destination-frame "#cancel-button"))

(defn add-destination-cleanup [add-destination-frame call-back]
  (seesaw-core/invoke-later
    (call-back)
    (actions-utils/close-window add-destination-frame)))

(defn add-destination [add-destination-frame call-back destination]
  (future
    (peer-service/add-peer-destination-if-missing destination)
    (peer-service/notify-peer-destination-if-necessary destination)
    (peer-service/download-peers)
    (add-destination-cleanup add-destination-frame call-back)))

(defn add-action [add-destination-frame e call-back]
  (controller-utils/disable-widget add-destination-frame)
  (add-destination add-destination-frame call-back (.getText (find-destination-text add-destination-frame))))

(defn attach-add-action [add-destination-frame call-back]
  (actions-utils/attach-frame-listener add-destination-frame "#add-button"
    #(add-action %1 %2 call-back)))

(defn attach [add-destination-frame call-back]
  (attach-add-action (attach-cancel-action add-destination-frame) call-back))

(defn show [main-frame call-back]
  (controller-utils/show (attach (add-destination-view/create main-frame) call-back)))