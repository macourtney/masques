(ns masques.view.main.tool-bar
  (:require [clj-internationalization.term :as term]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]
           [javax.swing JLabel ImageIcon]))

(def background-color (Color/GRAY))

(def logout-color "#FFAA00")
(def logout-font { :name "DIALOG" :style :bold :size 18 })
           
(defn create-masques-icon []
  (JLabel. (ImageIcon. (ClassLoader/getSystemResource "logo_for_light_backgrounds_small.png"))))
  
(defn create-icons-bar []
  (seesaw-core/flow-panel :id :icons-panel :items [] :align :center :hgap 10 :background background-color))

(defn logout []
  (seesaw-core/flow-panel
    :items [(seesaw-core/label :text (term/hello-user "") :id :hello-label :foreground logout-color)
            (seesaw-core/button :text (term/logout) :id :logout-button :foreground logout-color :border 0 :background background-color)]
    :background background-color))

(defn search []
  (seesaw-core/flow-panel :items ["search"] :background background-color))

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