(ns masques.controller.main.friend-tab
  (:require [masques.controller.actions.utils :as action-utils]
            [masques.controller.utils :as controller-utils]
            [masques.model.friend :as friends-model]
            [masques.view.main.friend-tab :as friend-tab-view]
            [seesaw.core :as seesaw-core]))

(defn find-friend-table [main-frame]
  (seesaw-core/select main-frame ["#friend-table"]))

(defn convert-to-table-friend [friend]
  friend)

(defn reload-table-data [main-frame]
  (when-let [friends (map convert-to-table-friend (friends-model/all-friends))]
    (seesaw-core/config! (find-friend-table main-frame)
      :model [:columns friend-tab-view/friend-table-columns
              :rows friends])))

(defn load-friend-table [main-frame]
  (reload-table-data main-frame)
  main-frame)

(defn find-add-button [main-frame]
  (seesaw-core/select main-frame ["#add-friend-button"]))

(defn attach-listener-to-add-button [main-frame]
  ;(action-utils/attach-listener main-frame "#add-button" 
  ;  (fn [e] (add-destination/show main-frame #(reload-table-data main-frame))))
  main-frame)

(defn attach-friend-listener [main-frame]
  main-frame)

(defn load-data [main-frame]
  (load-friend-table main-frame))

(defn attach [main-frame]
  (attach-friend-listener (attach-listener-to-add-button main-frame)))

(defn init [main-frame]
  (attach (load-data main-frame)))
