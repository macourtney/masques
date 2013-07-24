(ns masques.view.main.tool-bar
  (:require [seesaw.core :as seesaw-core])
  (:import [java.awt Color]
           [javax.swing JLabel ImageIcon]))

(def background-color (Color/GRAY))
           
(defn create-masques-icon []
  (JLabel. (ImageIcon. (ClassLoader/getSystemResource "logo_for_light_backgrounds_small.png"))))
  
(defn create-icons-bar []
  (seesaw-core/flow-panel :items ["icons bar"] :background background-color))

(defn create-global-actions-panel []
  (seesaw-core/flow-panel :items ["global actions bar"] :background background-color))

(defn create []
  (seesaw-core/border-panel
    :west (create-masques-icon)
    :center (create-icons-bar)
    :east (create-global-actions-panel)

    :background background-color))