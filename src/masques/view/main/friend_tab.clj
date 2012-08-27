(ns masques.view.main.friend-tab
  (:require [clj-internationalization.term :as term]
            [masques.model.friend :as friends-model]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table]))

(def tab-name (term/friend))

(def friend-table-columns [ { :key :name :text (term/handle) } ])

(defn create-friend-list-buttons []
  (seesaw-core/horizontal-panel :items 
    [ (seesaw-core/button :id :add-friend-button :text (term/add))
      [:fill-h 3]
      (seesaw-core/button :id :unfriend-button :text (term/unfriend))]))

(defn create-friend-list-header-panel []
  (seesaw-core/border-panel
    :west (term/friends)
    :east (create-friend-list-buttons)))

(defn create-friend-list-table []
  (seesaw-core/scrollable
    (seesaw-core/table :id :friend-table :preferred-size [600 :by 300]
      :model [ :columns friend-table-columns ])))

(defn create-friend-table-panel []
  (seesaw-core/border-panel
    :vgap 5
    :north (create-friend-list-header-panel)
    :center (create-friend-list-table)))

(defn create-friend-xml-buttons []
  (seesaw-core/horizontal-panel :items 
    [ (seesaw-core/button :id :save-text-button :text (term/save))
      [:fill-h 3]
      (seesaw-core/button :id :copy-text-button :text (term/copy)) ]))

(defn create-friend-xml-header-panel []
  (seesaw-core/border-panel
    :west (term/friend-text)
    :east (create-friend-xml-buttons)))

(defn create-friend-xml-text []
  (seesaw-core/scrollable
    (seesaw-core/text :id :friend-text :multi-line? true :editable? false :rows 4)))

(defn create-friend-xml-panel []
  (seesaw-core/border-panel
    :vgap 5
    :north (create-friend-xml-header-panel)
    :center (create-friend-xml-text)))

(defn create []
  (seesaw-core/vertical-panel
    :id :friend-tab-panel
    :border 9
    :items [(create-friend-xml-panel) [:fill-v 9] (create-friend-table-panel)]))

(defn friend-panel [main-frame]
  (seesaw-core/select main-frame ["#friend-tab-panel"]))

(defn find-friend-table [main-frame]
  (seesaw-core/select main-frame ["#friend-table"]))

(defn find-friend-xml-text [main-frame]
  (seesaw-core/select main-frame ["#friend-text"]))

(defn find-add-button [main-frame]
  (seesaw-core/select main-frame ["#add-friend-button"]))

(defn find-unfriend-button [main-frame]
  (seesaw-core/select main-frame ["#unfriend-button"]))

(defn find-copy-text-button [main-frame]
  (seesaw-core/select main-frame ["#copy-text-button"]))

(defn find-save-text-button [main-frame]
  (seesaw-core/select main-frame ["#save-text-button"]))

(defn convert-to-table-friend [friend]
  { :id (:id friend) :name (or (:name friend) (friends-model/friend-name friend)) })

(defn reset-friend-list [main-frame friends]
  (seesaw-core/config! (find-friend-table main-frame)
      :model [:columns friend-table-columns
              :rows (map convert-to-table-friend friends)]))

(defn add-friend [main-frame friend]
  (seesaw-table/insert-at! (find-friend-table main-frame) 0 (convert-to-table-friend friend)))

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

(defn delete-friend [main-frame friend]
  (seesaw-table/remove-at! (find-friend-table main-frame) (find-friend-index main-frame friend)))

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

(defn load-friend-xml-text [main-frame text]
  (.setText (find-friend-xml-text main-frame) text)
  main-frame)

(defn friend-xml-text [main-frame]
  (.getText (find-friend-xml-text main-frame)))

(defn click-unfriend-button
  "Clicks the unfriend button."
  [main-frame]
  (.doClick (find-unfriend-button main-frame)))

(defn click-copy-text-button
  "Clicks the copy text button."
  [main-frame]
  (.doClick (find-copy-text-button main-frame)))