(ns masques.controller.friend.view
  (:require [masques.controller.utils :as controller-utils]
            [masques.view.friend.view :as friend-view]))

(defn attach [friend-panel]
  friend-panel)

(defn show [main-frame]
  (controller-utils/show (attach (friend-view/create main-frame))))