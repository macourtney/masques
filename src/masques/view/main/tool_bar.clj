(ns masques.view.main.tool-bar
  (:require [clj-internationalization.term :as term]
            [seesaw.border :as seesaw-border]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]
           [javax.swing JLabel ImageIcon]))

(def background-color (Color/GRAY))
(def search-background-color (Color/LIGHT_GRAY))

(def logout-color "#FFAA00")
(def logout-font { :name "DIALOG" :style :bold :size 18 })
           
(defn create-masques-icon []
  (JLabel. (ImageIcon. (ClassLoader/getSystemResource "logo_for_light_backgrounds_small.png"))))
  
(defn create-icons-bar []
  (seesaw-core/flow-panel :id :icons-panel :items [] :align :center :hgap 10 :background background-color))

(defn logout []
  (seesaw-core/flow-panel
    :items [(seesaw-core/label :id :hello-label :text (term/hello-user "") :foreground logout-color :font logout-font)
            (seesaw-core/button :id :logout-button :text (term/logout) :foreground logout-color :font logout-font :border 0 :background background-color)]
    :background background-color))

(defn search-text-panel []
  (seesaw-core/flow-panel
    :items [(seesaw-core/label :id :search-label :text (term/search) :background search-background-color)
            (seesaw-core/text :id :search-text :columns 10 :background search-background-color)]
    :background search-background-color))

(defn search-radios-panel []
  (seesaw-core/flow-panel
    :items [(seesaw-core/radio :id :search-shares-radio :text (term/shares) :background search-background-color)
            (seesaw-core/radio :id :search-friends-radio :text (term/friends) :background search-background-color)]

    :background search-background-color
    :border (seesaw-border/line-border :top 1)))

(defn search []
  (seesaw-core/flow-panel
      :items [(seesaw-core/border-panel
                :north (search-text-panel)
                :south (search-radios-panel)

                :border 5
                :background search-background-color)]
      
    :background background-color))

(defn create-global-actions-panel []
  (seesaw-core/border-panel
    :west (search)
    :east (logout)

    :background background-color))

(defn create []
  (seesaw-core/border-panel
    :west (create-masques-icon)
    :center (create-icons-bar)
    :east (create-global-actions-panel)

    :background background-color
    :border 5
    :preferred-size [800 :by 105]))