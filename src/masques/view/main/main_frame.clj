(ns masques.view.main.main-frame
  (:require [clj-internationalization.term :as term]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]))

(defn create-tool-bar []
  (seesaw-core/flow-panel :items ["tool bar"]))
  
(defn create-status-panel []
  (seesaw-core/flow-panel :items ["status panel"]))
  
(defn create-display-panel []
  (seesaw-core/flow-panel :items ["display panel"]))

(defn create-footer []
  (seesaw-core/border-panel
    :east (seesaw-core/label :text (term/masques-version) :foreground (Color/WHITE))
    :background (Color/GRAY)

    :border 5))
  
(defn create-content-panel []
  (seesaw-core/border-panel
    :north (create-tool-bar)
    :west (create-status-panel)
    :east (create-display-panel)
    :south (create-footer)))
            
(defn create []
  (view-utils/center-window
    (seesaw-core/frame
      :title (term/masques)
      :content (create-content-panel)
      :on-close :exit
      :visible? false)))