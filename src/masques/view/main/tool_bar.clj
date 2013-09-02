(ns masques.view.main.tool-bar
  (:require [clj-internationalization.term :as term]
            [masques.controller.main.panel-protocol :as panel-protocol]
            [masques.view.utils :as view-utils]
            [seesaw.border :as seesaw-border]
            [seesaw.core :as seesaw-core]
            [seesaw.mig :as seesaw-mig])
  (:import [java.awt Color]
           [javax.swing JLabel ImageIcon]))

(def background-color (Color/GRAY))
(def search-background-color (Color/LIGHT_GRAY))

(def logout-color "#FFAA00")
(def logout-font { :name "DIALOG" :style :bold :size 18 })

(def id "tool-bar")
(def icons-panel-id "icons-panel")

(defn create-masques-icon []
  (JLabel. (ImageIcon. (ClassLoader/getSystemResource "logo_for_dark_backgrounds_small.png"))))

(defn create-icons-bar []
  (seesaw-core/scrollable
    (seesaw-core/horizontal-panel :id icons-panel-id :items [] :background background-color :border 5)

    :border 0))

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
    :id (.substring id 0)

    :west (create-masques-icon)
    :center (create-icons-bar)
    :east (create-global-actions-panel)

    :background background-color
    :border (seesaw-border/compound-border
              (seesaw-border/empty-border :thickness 5)
              (seesaw-border/line-border :thickness 1 :color (Color/LIGHT_GRAY))
              (seesaw-border/line-border :thickness 1 :color background-color))
    :preferred-size [800 :by 105]))

(defn create-icon-button
  "Creates the icon button for the given panel. If the panel does not have an icon, then the text of the button is set
to the panel's name."
  [panel]
  (let [icon (panel-protocol/icon panel)
        button (seesaw-core/button :id (str "icon-" panel) :icon icon :border 0 :background background-color)]
    (when (not icon)
      (seesaw-core/config! button :text (panel-protocol/panel-name panel)))
    button))

(defn find-icons-panel [tool-bar]
  (view-utils/find-component tool-bar (str "#" icons-panel-id)))

(defn add-icon
  "Adds the given panel as an icon on the tool-bar."
  [tool-bar panel]
  (let [icons-panel (find-icons-panel tool-bar)
        current-icons (seesaw-core/config icons-panel :items)]
    (seesaw-core/config! icons-panel
                         :items (concat current-icons [[:fill-h 5] (create-icon-button panel)]))))