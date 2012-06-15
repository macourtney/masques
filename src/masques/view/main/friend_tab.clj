(ns masques.view.main.friend-tab
  (:require [clj-internationalization.core :as clj-i18n]
            [seesaw.core :as seesaw-core]))

(def tab-name (clj-i18n/friend))

(def friend-table-columns [ { :key :handle :text (clj-i18n/handle) } ])

(defn create-friend-list-buttons []
  (seesaw-core/horizontal-panel :items 
    [ (seesaw-core/button :id :add-friend-button :text (clj-i18n/add))
      [:fill-h 3]
      (seesaw-core/button :id :unfriend-button :text (clj-i18n/unfriend))]))

(defn create-friend-list-header-panel []
  (seesaw-core/border-panel
    :west (clj-i18n/friends)
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
    [ (seesaw-core/button :id :save-text-button :text (clj-i18n/save))
      [:fill-h 3]
      (seesaw-core/button :id :copy-text-button :text (clj-i18n/copy)) ]))

(defn create-friend-xml-header-panel []
  (seesaw-core/border-panel
    :west (clj-i18n/friend-text)
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
    :border 9
    :items [(create-friend-xml-panel) [:fill-v 9] (create-friend-table-panel)]))