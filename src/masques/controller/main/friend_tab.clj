(ns masques.controller.main.friend-tab
  (:require [clojure.java.io :as java-io]
            [masques.controller.actions.utils :as action-utils]
            [masques.controller.friend.add :as add-friend-view]
            [masques.controller.utils :as controller-utils]
            [masques.model.clipboard :as clipboard-model]
            [masques.model.friend :as friends-model]
            [masques.model.identity :as identity-model]
            [masques.view.main.friend-tab :as friend-tab-view])
  (:import [javax.swing JFileChooser]))

(def friend-add-listener-key "friend-add-listener")
(def friend-delete-listener-key "friend-delete-listener")

(defn reload-table-data [main-frame]
  (when-let [friends (friends-model/all-friends)]
    (friend-tab-view/reset-friend-list main-frame friends)))

(defn load-friend-table [main-frame]
  (reload-table-data main-frame)
  main-frame)

(defn load-friend-xml-text [main-frame]
  (friend-tab-view/load-friend-xml-text main-frame (friends-model/friend-xml-string)))

(defn save-text-to-clipboard [main-frame]
  (clipboard-model/save-to-clipboard! (friend-tab-view/friend-xml-text main-frame)))

(defn attach-listener-to-copy-text-button [main-frame]
  (action-utils/attach-listener main-frame "#copy-text-button" 
    (fn [e] (save-text-to-clipboard main-frame))))

(defn save-friend-xml [main-frame file]
  (java-io/copy (friend-tab-view/friend-xml-text main-frame) file))

(defn save-text-listener [main-frame]
  (when-let [file (controller-utils/choose-file main-frame)]
    (save-friend-xml main-frame file)))

(defn attach-listener-to-save-text-button [main-frame]
  (action-utils/attach-listener main-frame "#save-text-button" 
    (fn [e] (save-text-listener main-frame))))

(defn attach-listener-to-add-button [main-frame]
  (action-utils/attach-listener main-frame "#add-friend-button" 
    (fn [e] (add-friend-view/show main-frame))))

(defn unfriend-selected
  "Unfriends the selected friend in the friend table."
  [main-frame]
  (friends-model/destroy-record (friend-tab-view/find-selected-friend main-frame)))

(defn attach-listener-to-unfriend-button [main-frame]
  (action-utils/attach-listener main-frame "#unfriend-button" 
    (fn [e] (unfriend-selected main-frame))))

(defn friend-add-listener [main-frame friend]
  (let [new-friend (friends-model/get-record (:id friend))]
    (when (= (:identity_id new-friend) (:id (identity-model/current-user-identity)))
      (friend-tab-view/add-friend main-frame new-friend))))

(defn attach-friend-listener [main-frame]
  (controller-utils/attach-and-detach-listener main-frame #(friend-add-listener main-frame %) friend-add-listener-key
                                               friend-tab-view/friend-panel friends-model/add-friend-add-listener
                                               friends-model/remove-friend-add-listener)
  (controller-utils/attach-and-detach-listener main-frame #(friend-tab-view/delete-friend main-frame %)
                                               friend-delete-listener-key friend-tab-view/friend-panel
                                               friends-model/add-friend-delete-listener
                                               friends-model/remove-friend-delete-listener)
  main-frame)

(defn load-data [main-frame]
  (load-friend-table (load-friend-xml-text main-frame)))

(defn attach [main-frame]
  (attach-listener-to-copy-text-button
    (attach-friend-listener
      (attach-listener-to-unfriend-button
        (attach-listener-to-add-button main-frame)))))

(defn init [main-frame]
  (attach (load-data main-frame)))
