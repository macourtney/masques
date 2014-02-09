(ns masques.view.main.main-frame
  (:require [clj-internationalization.term :as term]
            [masques.view.main.display-panel :as display-panel]
            [masques.view.main.status-panel :as status-panel]
            [masques.view.main.tool-bar :as tool-bar]
            [masques.view.utils :as view-utils]
            [seesaw.border :as seesaw-border]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]))

(def footer-background-color (Color/GRAY))

(def settings-color "#FFAA00")
(def settings-font { :name "DIALOG" :style :bold :size 18 })

(defn create-footer []
  (seesaw-core/border-panel
    :west (seesaw-core/button :id :settings-button :text (term/settings) :foreground settings-color :font settings-font
                              :border 0 :background footer-background-color)
    :east (seesaw-core/label :text (term/masques-version) :foreground (Color/WHITE))
    :background footer-background-color
    :border (seesaw-border/compound-border
              (seesaw-border/empty-border :thickness 5)
              (seesaw-border/line-border :thickness 1 :color (Color/LIGHT_GRAY))
              (seesaw-border/line-border :thickness 1 :color footer-background-color))))
  
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

(defn find-display-panel
  "Returns the display panel of the given main frame."
  [main-frame]
  (view-utils/find-component main-frame (str "#" display-panel/id)))

(defn find-tool-bar
  "Returns the tool bar of the given main frame."
  [main-frame]
  (view-utils/find-component main-frame (str "#" tool-bar/id)))

(defn add-panel
  "Adds the given panel to the main frame. Adds the icon to the tool bar and the panel's view to the display panel."
  [main-frame panel panel-button-listener]
  (tool-bar/add-icon (find-tool-bar main-frame) panel panel-button-listener)
  (display-panel/add-panel (find-display-panel main-frame) panel))

(defn show-panel
  "Shows the given panel (or panel id)."
  [main-frame panel args]
  (tool-bar/select-icon-button (find-tool-bar main-frame) panel)
  (display-panel/show-panel (find-display-panel main-frame) panel (or args [])))
