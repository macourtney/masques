(ns masques.view.friend.panel
  (:require [clj-internationalization.term :as term]
            [masques.view.utils :as view-utils]
            [seesaw.border :as seesaw-border]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core]))

(def link-button-font { :name "DIALOG" :style :plain :size 12 })

(defn create-link-button [id text]
  (seesaw-core/button :id id :text text :border 0 :font link-button-font))

(defn create-search-fields-panel []
  (seesaw-core/vertical-panel
    :items [(seesaw-core/border-panel
              :west (seesaw-core/horizontal-panel
                      :items [(term/friend)
                              [:fill-h 5]
                              (seesaw-core/text :id :alias-search-text
                                                :columns 10)
                              [:fill-h 5]
                              (term/alias-or-nick)])
              :east (create-link-button
                      :clear-search-button (term/clear-search)))
            (seesaw-core/border-panel
              :west (seesaw-core/horizontal-panel
                      :items [(term/group)
                              [:fill-h 5]
                              (seesaw-core/combobox
                                :id :group-search-combobox
                                :preferred-size  [100 :by 20])])
              :center (seesaw-core/horizontal-panel
                        :items [(term/added)
                                [:fill-h 5]
                                (seesaw-core/text :id :added-to-1-search-text
                                                  :columns 10)
                                [:fill-h 5]
                                (term/to)
                                [:fill-h 5]
                                (seesaw-core/text :id :added-to-2-search-text
                                                  :columns 10)])
              :east (create-link-button
                      :search-friends-button (term/search-friends))
              :hgap 15)]
    
    :border [15 (seesaw-border/line-border :thickness 1)]))

(defn create-search-panel []
  (seesaw-core/border-panel
    :center (create-search-fields-panel)
    :south (seesaw-core/border-panel
             :west (create-link-button :advanced-friend-search-button
                                       (term/advanced-friend-search)))
    
    :vgap 5))

(defn create-all-friends-table []
  (seesaw-core/scrollable
    (seesaw-core/table :model [:columns [""  :Alias :Nick :Groups ""]])))

(defn create-all-friends-tab []
  (seesaw-core/border-panel
    :north (seesaw-core/border-panel
             :west (create-link-button :send-friend-request-button
                                       (term/send-friend-request)))
    :center (create-all-friends-table)

    :vgap 5
    :border 15))

(defn create-pending-friend-requests-table []
  (seesaw-core/scrollable
    (seesaw-core/table :model [:columns [""  :Alias :Message ""]])))

(defn create-pending-friend-requests-tab []
  (seesaw-core/border-panel
    :center (create-pending-friend-requests-table)

    :border 15))

(defn create-body-panel []
  (seesaw-core/tabbed-panel
    :tabs [{ :title (term/all-friends) :content (create-all-friends-tab) }
           { :title (term/pending-friend-requests) :content (create-pending-friend-requests-tab) }]))

(defn create []
  (seesaw-core/border-panel
    :id "friend-panel"

    :north (create-search-panel)
    :center (create-body-panel)

    :vgap 10
    :border 11))