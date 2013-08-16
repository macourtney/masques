(ns masques.view.main.status-panel
  (:require [clj-internationalization.term :as term]
            [seesaw.border :as seesaw-border]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]))

(def status-background-color (seesaw-color/color 238 238 238))
(def status-button-font { :name "DIALOG" :style :plain :size 10 })

(defn create-update-status []
  (seesaw-core/border-panel
    :north (seesaw-core/scrollable (seesaw-core/text :id :status-text :multi-line? true :wrap-lines? true :rows 4) :preferred-size [200 :by 75])
    :south
      (seesaw-core/border-panel
        :west (seesaw-core/button :id :update-status-button :text (term/update-status) :border 0 :background status-background-color :font status-button-font)
        :east (seesaw-core/button :id :create-new-share-button :text (term/create-new-share) :border 0 :background status-background-color :font status-button-font)
        
        :hgap 3)
        
    :vgap 3))
  
(defn create-recent-shares []
  (seesaw-core/flow-panel :items ["recent shares"]))

(defn create-online-friends []
  (seesaw-core/flow-panel :items ["global actions bar"]))

(defn create []
  (seesaw-core/border-panel
    :north (create-update-status)
    :center (create-recent-shares)
    :south (create-online-friends)

    :vgap 3
    :border (seesaw-border/compound-border
              (seesaw-border/empty-border :thickness 10)
              (seesaw-border/line-border :right 1))))