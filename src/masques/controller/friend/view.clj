(ns masques.controller.friend.view
  (:refer-clojure :exclude [load])
  (:require [masques.controller.actions.utils :as actions-utils]
            [masques.controller.utils :as controller-utils]
            [masques.model.friend :as friend-model]
            [masques.service.calls.profile :as profile-call]
            [masques.view.friend.view :as friend-view]))

(defn attach-done-action [friend-panel]
  (actions-utils/attach-window-close-listener friend-panel "#done-button"))

(defn attach [friend-panel]
  (attach-done-action friend-panel))

(defn friend-id [friend]
  (if (map? friend)
    (:id friend)
    friend))

(defn profile [friend]
  (profile-call/profile (friend-model/find-record { :id (friend-id friend) })))

(defn load [friend friend-panel]
  (friend-view/load (profile friend) friend-panel) 
  friend-panel)

(defn show [main-frame friend]
  (controller-utils/show (load friend (attach (friend-view/create main-frame)))))

(defn scrape-profile [main-frame]
  (friend-view/scrape-profile main-frame))

(defn click-done [main-frame]
  (when main-frame
    (.doClick (friend-view/find-done-button main-frame))))