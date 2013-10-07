(ns masques.view.stream.panel
  (:require [clj-internationalization.term :as term]
            [masques.view.utils :as view-utils]
            [seesaw.border :as seesaw-border]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]))

(def field-size [150 :by 25])
(def button-size [100 :by 25])
(def date-range-text-size [50 :by 25])

(def group-background-color (seesaw-color/color 238 238 238))
(def group-button-font { :name "DIALOG" :style :plain :size 10 })

(defn create-button
  ([id text]
    (seesaw-core/button :id id :text text :border 0 :font group-button-font :background group-background-color))
  ([id text size]
    (let [button (create-button id text)]
      (seesaw-core/config! button :preferred-size size :maximum-size size)
      button)))

(defn create-group-panel []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/group)
            (seesaw-core/combobox :id :group-combobox :preferred-size field-size :maximum-size field-size)
            (create-button :hide-search-box (term/hide-search-box) button-size)]))

(defn create-share-type-panel []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/share-type)
            (seesaw-core/combobox :id :share-type-combobox :preferred-size field-size :maximum-size field-size)
            (create-button :clear-search (term/clear-search) button-size)]))

(defn create-friend-panel []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/friend)
            (seesaw-core/text :id :friend-text :preferred-size field-size :maximum-size field-size)
            (seesaw-core/flow-panel :items [] :preferred-size button-size :maximum-size button-size)]))

(defn create-date-range-panel []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/date-range)
            (seesaw-core/flow-panel
              :items [(seesaw-core/text :id :begin-date-text :preferred-size date-range-text-size :maximum-size date-range-text-size)
                      (term/to)
                      (seesaw-core/text :id :end-date-text :preferred-size date-range-text-size :maximum-size date-range-text-size)]
              :preferred-size field-size
              :maximum-size field-size
              :border 0)
            (seesaw-core/flow-panel :items [] :preferred-size button-size :maximum-size button-size)]
    :border 0))

(defn create-search-terms-panel []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/search-terms)
            (seesaw-core/text :id :search-terms-text :preferred-size field-size :maximum-size field-size)
            (create-button :search-stream (term/search-stream) button-size)]))

(defn create-search []
  (seesaw-core/vertical-panel
      :items [(create-group-panel) [:fill-v 3] (create-share-type-panel) [:fill-v 3] (create-friend-panel) [:fill-v 3]
              (create-date-range-panel) [:fill-v 3] (create-search-terms-panel)]

      :border (seesaw-border/line-border :thickness 1 :color (Color/GRAY))))

(defn create-header []
  (seesaw-core/border-panel
    :west (create-search)
    :east (seesaw-core/vertical-panel :items [(seesaw-core/label :text (term/stream) :foreground "#380B61" :font { :size 48 })])))

(defn create-stream-buttons []
  (seesaw-core/horizontal-panel
    :items [(create-button :filter-all-shares (term/all-shares)) " | " (create-button :inbox (term/inbox)) " | " (create-button :from-me (term/from-me))]))

(defn create-stream-list []
  (seesaw-core/scrollable
     (seesaw-core/listbox :id :stream-listbox
         ; :model A ListModel, or a sequence of values with which a DefaultListModel will be constructed.
         ; :renderer A cell renderer to use. See (seesaw.cells/to-cell-renderer).
     )))

(defn create-body []
  (seesaw-core/border-panel
    :north (create-stream-buttons)
    :center (create-stream-list)
    
    :vgap 5))

(defn create []
  (seesaw-core/border-panel
    :north (create-header)
    :center (create-body)
    
    :vgap 10
    :border 11))