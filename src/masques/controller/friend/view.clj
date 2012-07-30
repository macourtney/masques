(ns masques.controller.friend.view
  (:require [masques.controller.utils :as controller-utils]
            [masques.model.friend :as friend-model]
            [masques.service.calls.profile :as profile-call]
            [masques.view.friend.view :as friend-view]))

(defn attach [friend-panel]
  friend-panel)

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