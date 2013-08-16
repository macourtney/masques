(ns masques.view.main.status-panel
  (:require [clj-internationalization.term :as term]
            [seesaw.border :as seesaw-border]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]))

(def panel-width 250)
  
(def status-background-color (seesaw-color/color 238 238 238))
(def status-button-font { :name "DIALOG" :style :plain :size 10 })

(def title-color (seesaw-color/color 100 100 100))
(def title-font { :name "DIALOG" :style :bold :size 14 })

(defn create-update-status []
  (seesaw-core/border-panel
    :north (seesaw-core/scrollable (seesaw-core/text :id :status-text :multi-line? true :wrap-lines? true :rows 4) :preferred-size [panel-width :by 75])
    :south
      (seesaw-core/border-panel
        :west (seesaw-core/button :id :update-status-button :text (term/update-status) :border 0 :background status-background-color :font status-button-font)
        :east (seesaw-core/button :id :create-new-share-button :text (term/create-new-share) :border 0 :background status-background-color :font status-button-font)
        
        :hgap 3)
        
    :vgap 3))
  
(defn create-recent-shares []
  (seesaw-core/border-panel
    :north (seesaw-core/border-panel
             :west (seesaw-core/label :text (term/recent-shares) :foreground title-color :font title-font)
             :center (seesaw-core/button :id :inbox-button :text (term/inbox) :border 0 :background status-background-color :font status-button-font)
             :east (seesaw-core/button :id :filter-button :text (term/filter) :font status-button-font)

             :hgap 3)
    :south (seesaw-core/scrollable
             (seesaw-core/listbox :id :recent-shares-listbox
                 ; :model A ListModel, or a sequence of values with which a DefaultListModel will be constructed.
                 ; :renderer A cell renderer to use. See (seesaw.cells/to-cell-renderer).
             )
             :preferred-size [panel-width :by 200])

    :vgap 3))

(defn create-online-friends []
  (seesaw-core/flow-panel :items ["global actions bar"]))

(defn create []
  (seesaw-core/border-panel
    :north (create-update-status)
    :center (create-recent-shares)
    :south (create-online-friends)

    :vgap 15
    :border (seesaw-border/compound-border
              (seesaw-border/empty-border :thickness 10)
              (seesaw-border/line-border :right 1))))