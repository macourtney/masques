(ns masques.controller.main.group-tab
  (:require [clojure.java.io :as java-io]
            [masques.controller.actions.utils :as action-utils]
            [masques.controller.group.add :as group-add-controller]
            [masques.controller.utils :as controller-utils]
            [masques.model.friend :as friend-model]
            [masques.model.group :as group-model]
            [masques.model.identity :as identity-model]
            [masques.view.main.group-tab :as group-tab-view])
  (:import [javax.swing JFileChooser]))

(def new-group-listener-key "new-group-listener")
(def delete-group-listener-key "delete-group-listener")

(defn reload-member-table-data [main-frame]
  (when-let [selected-group (group-tab-view/selected-group main-frame)]
    (group-tab-view/reset-member-table main-frame (friend-model/group-friends selected-group))))

(defn reload-group-list [main-frame]
  (when-let [groups (group-model/find-identity-groups)]
    (group-tab-view/set-groups main-frame groups)))

(defn load-group-list [main-frame]
  (reload-group-list main-frame)
  main-frame)

(defn group-selection-listener [main-frame list-selection-event]
  (reload-member-table-data main-frame))

(defn attach-group-selection-listner [main-frame]
  (group-tab-view/add-group-list-selection-listener main-frame #(group-selection-listener main-frame %)))

;(defn save-friend-xml [main-frame file]
;  (java-io/copy (group-tab-view/friend-xml-text main-frame) file))

;(defn save-text-listener [main-frame]
;  (when-let [file (controller-utils/choose-file main-frame)]
;    (save-friend-xml main-frame file)))

;(defn attach-listener-to-save-text-button [main-frame]
;  (action-utils/attach-listener main-frame "#save-text-button" 
;    (fn [e] (save-text-listener main-frame))))

(defn attach-listener-to-add-button [main-frame]
  (action-utils/attach-listener main-frame "#new-group-button" 
    (fn [e] (group-add-controller/show main-frame))))

(defn delete-selected-group
  "Deletes the selected group."
  [main-frame e]
  (group-model/destroy-record (group-tab-view/selected-group main-frame)))

(defn attach-listener-to-delete-group-button [main-frame]
  (group-tab-view/attach-listener-to-delete-group-button main-frame #(delete-selected-group main-frame %)))

(defn group-add-listener [main-frame group]
  (let [new-group (group-model/get-record (:id group))]
    (when (= (:identity_id new-group) (:id (identity-model/current-user-identity)))
      (group-tab-view/add-group main-frame new-group))))

(defn attach-group-listener [main-frame]
  (controller-utils/attach-and-detach-listener main-frame #(group-add-listener main-frame %) new-group-listener-key
                                               group-tab-view/group-panel group-model/add-group-add-listener
                                               group-model/remove-group-add-listener)
  (controller-utils/attach-and-detach-listener main-frame #(group-tab-view/remove-group main-frame %)
                                               delete-group-listener-key group-tab-view/group-panel
                                               group-model/add-group-delete-listener
                                               group-model/remove-group-delete-listener)
  main-frame)

(defn load-data [main-frame]
  (load-group-list main-frame))

(defn attach [main-frame]
  (attach-listener-to-add-button
    (attach-group-selection-listner
      (attach-group-listener
        (attach-listener-to-delete-group-button
          main-frame)))))

(defn init [main-frame]
  (load-data (attach main-frame)))
