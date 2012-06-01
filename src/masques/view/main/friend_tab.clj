(ns masques.view.main.friend-tab
  (:require [clj-internationalization.core :as clj-i18n]
            [seesaw.core :as seesaw-core]))

(def tab-name (clj-i18n/friend))

(def friend-table-columns [ { :key :handle :text (clj-i18n/handle) }
                            { :key :name :text (clj-i18n/full-name) } ])

(defn create-friend-list-buttons []
  (seesaw-core/horizontal-panel :items 
    [ (seesaw-core/button :id :add-friend-button :text (clj-i18n/add)) ]))

(defn create-friend-list-header-panel []
  (seesaw-core/border-panel
    :west (clj-i18n/friends)
    :east (create-friend-list-buttons)))

(defn create-friend-list-table []
  (seesaw-core/scrollable
    (seesaw-core/table :id :friend-table :preferred-size [600 :by 300]
      :model [ :columns friend-table-columns ])))

(defn create []
  (seesaw-core/border-panel
    :border 9
    :vgap 5
    :north (create-friend-list-header-panel)
    :center (create-friend-list-table)))