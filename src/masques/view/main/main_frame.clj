(ns masques.view.main.main-frame
  (:require [clj-internationalization.term :as term]
            [masques.view.main.display-panel :as display-panel]
            [masques.view.main.status-panel :as status-panel]
            [masques.view.main.tool-bar :as tool-bar]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]))

(defn create-footer []
  (seesaw-core/border-panel
    :east (seesaw-core/label :text (term/masques-version) :foreground (Color/WHITE))
    :background (Color/GRAY)
    :border 5))
  
(defn create-content-panel []
  (seesaw-core/border-panel
    :north (tool-bar/create)
    :west (status-panel/create)
    :center (display-panel/create)
    :south (create-footer)))
            
(defn create []
  (view-utils/center-window
    (seesaw-core/frame
      :title (term/masques)
      :content (create-content-panel)
      :on-close :exit
      :visible? false)))