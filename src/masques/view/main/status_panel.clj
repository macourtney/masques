(ns masques.view.main.status-panel
  (:require [clj-internationalization.term :as term]
            [masques.view.utils :as view-utils]
            [seesaw.border :as seesaw-border]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]))

(def panel-width 250)

(def status-background-color (seesaw-color/color 238 238 238))
(def status-button-font { :name "DIALOG" :style :plain :size 10 })

(def title-color (seesaw-color/color 100 100 100))
(def title-font { :name "DIALOG" :style :bold :size 14 })

(defn status-button
  "Creates a link button with a font for the status panel."
  [id text]
  (view-utils/create-link-button :id id :text text :font status-button-font))

(defn create-update-status []
  (seesaw-core/border-panel
    :north
      (seesaw-core/scrollable
        (seesaw-core/text :id :status-text :multi-line? true :wrap-lines? true
                          :rows 4)
        :preferred-size [panel-width :by 75])
    :south
      (seesaw-core/border-panel
        :west (status-button :update-status-button (term/update-status))
        :east (status-button :create-new-share-button (term/create-new-share))
        :hgap 3) 
    :vgap 3))
  
(defn create-recent-shares []
  (seesaw-core/border-panel
    :north (seesaw-core/border-panel
             :west (seesaw-core/label :text (term/recent-shares)
                                      :foreground title-color :font title-font)
             :center (status-button :inbox-button (term/inbox))
             :east (status-button :filter-button (term/filter))
             :hgap 3)
    :south (seesaw-core/scrollable
             (seesaw-core/listbox :id :recent-shares-listbox
                 ; :model A ListModel, or a sequence of values with which a DefaultListModel will be constructed.
                 ; :renderer A cell renderer to use. See (seesaw.cells/to-cell-renderer).
             )
             :preferred-size [panel-width :by 200])
    :vgap 3))

(defn create-online-friends []
  (seesaw-core/border-panel
    :north (seesaw-core/border-panel
             :west (seesaw-core/label :text (term/online-friends)
                                      :foreground title-color :font title-font)
             :east (status-button :filter-online-friends-button (term/filter))
             :hgap 3)
    :south (seesaw-core/scrollable
             (seesaw-core/listbox :id :online-friends-listbox
                 ; :model A ListModel, or a sequence of values with which a DefaultListModel will be constructed.
                 ; :renderer A cell renderer to use. See (seesaw.cells/to-cell-renderer).
             )
             :preferred-size [panel-width :by 200])
    :vgap 3))

(defn create []
  (seesaw-core/border-panel
    :north (create-update-status)
    :center (create-recent-shares)
    :south (create-online-friends)

    :vgap 15
    :border (seesaw-border/compound-border
              (seesaw-border/empty-border :thickness 10)
              (seesaw-border/line-border :right 1))))