(ns masques.controller.main.friend-tab
  (:require [masques.controller.actions.utils :as action-utils]
            [masques.controller.add-friend.view :as add-friend-view]
            [masques.controller.utils :as controller-utils]
            [masques.model.friend :as friends-model]
            [masques.model.identity :as identity-model]
            [masques.view.main.friend-tab :as friend-tab-view]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table]))

(def friend-add-listener-key "friend-add-listener")
(def friend-delete-listener-key "friend-delete-listener")

(defn friend-panel [main-frame]
  (seesaw-core/select main-frame ["#friend-tab-panel"]))

(defn find-friend-table [main-frame]
  (seesaw-core/select main-frame ["#friend-table"]))

(defn find-friend-xml-text [main-frame]
  (seesaw-core/select main-frame ["#friend-text"]))

(defn convert-to-table-friend [friend]
  { :id (:id friend) :name (friends-model/friend-name friend) })

(defn reload-table-data [main-frame]
  (when-let [friends (map convert-to-table-friend (friends-model/all-friends))]
    (seesaw-core/config! (find-friend-table main-frame)
      :model [:columns friend-tab-view/friend-table-columns
              :rows friends])))

(defn friend-count
  "Returns the number of rows in the friend table."
  [main-frame]
  (seesaw-table/row-count (find-friend-table main-frame)))

(defn all-friends
  "Returns all of the friends from the friend table."
  [main-frame]
  (seesaw-table/value-at (find-friend-table main-frame) (range (friend-count main-frame))))

(defn all-friend-pairs
  "Returns all the friends from the friend table as pairs with the index of the friend in the table."
  [main-frame]
  (map #(list %1 %2) (range) (all-friends main-frame)))

(defn find-friend-pair
  "Returns the friend pair for the given friend if it is in the table."
  [main-frame friend]
  (when-let [friend-id (if (map? friend) (:id friend) friend)]
    (first (filter #(= friend-id (:id (second %))) (all-friend-pairs main-frame)))))

(defn find-friend-index
  "Returns the index of the given friend."
  [main-frame friend]
  (when-let [found-friend-pair (find-friend-pair main-frame friend)]
    (first found-friend-pair)))

(defn find-selected-friend
  "Returns the selected friend in the friend table."
  [main-frame]
  (when-let [friend-table (find-friend-table main-frame)]
    (let [selected-row (.getSelectedRow friend-table)]
      (when (>= selected-row 0)
        (seesaw-table/value-at friend-table selected-row)))))

(defn set-selected-friend
  "Selects the given friend in the friend table."
  [main-frame friend]
  (when-let [friend-index (find-friend-index main-frame friend)]
    (when (>= friend-index 0)
      (.setRowSelectionInterval (find-friend-table main-frame) friend-index friend-index))))

(defn friend-xml-text [main-frame]
  (seesaw-core/text (find-friend-xml-text main-frame)))

(defn load-friend-table [main-frame]
  (reload-table-data main-frame)
  main-frame)

(defn load-friend-xml-text [main-frame]
  (.setText (find-friend-xml-text main-frame)
        (friends-model/friend-xml-string))
  main-frame)

(defn find-add-button [main-frame]
  (seesaw-core/select main-frame ["#add-friend-button"]))

(defn find-unfriend-button [main-frame]
  (seesaw-core/select main-frame ["#unfriend-button"]))

(defn click-unfriend-button
  "Clicks the unfriend button."
  [main-frame]
  (.doClick (find-unfriend-button main-frame)))

(defn attach-listener-to-add-button [main-frame]
  (action-utils/attach-listener main-frame "#add-friend-button" 
    (fn [e] (add-friend-view/show main-frame))))

(defn unfriend-selected
  "Unfriends the selected friend in the friend table."
  [main-frame]
  (friends-model/destroy-record (find-selected-friend main-frame)))

(defn attach-listener-to-unfriend-button [main-frame]
  (action-utils/attach-listener main-frame "#unfriend-button" 
    (fn [e] (unfriend-selected main-frame))))

(defn friend-add-listener [main-frame friend]
  (let [new-friend (friends-model/get-record (:id friend))]
    (when (= (:identity_id new-friend) (:id (identity-model/current-user-identity)))
      (seesaw-table/insert-at! (find-friend-table main-frame) 0 (convert-to-table-friend new-friend)))))

(defn friend-delete-listener [main-frame friend]
  (seesaw-table/remove-at! (find-friend-table main-frame) (find-friend-index main-frame friend)))

(defn attach-friend-listener [main-frame]
  (controller-utils/attach-and-detach-listener main-frame #(friend-add-listener main-frame %) friend-add-listener-key
                                               friend-panel friends-model/add-friend-add-listener
                                               friends-model/remove-friend-add-listener)
  (controller-utils/attach-and-detach-listener main-frame #(friend-delete-listener main-frame %)
                                               friend-delete-listener-key friend-panel
                                               friends-model/add-friend-delete-listener
                                               friends-model/remove-friend-delete-listener)
  main-frame)

(defn load-data [main-frame]
  (load-friend-table (load-friend-xml-text main-frame)))

(defn attach [main-frame]
  (attach-friend-listener (attach-listener-to-unfriend-button (attach-listener-to-add-button main-frame))))

(defn init [main-frame]
  (attach (load-data main-frame)))
