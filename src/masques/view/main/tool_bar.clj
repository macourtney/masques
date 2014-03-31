(ns masques.view.main.tool-bar
  (:require [clj-internationalization.term :as term]
            [masques.controller.main.panel-protocol :as panel-protocol]
            [masques.model.profile :as profile-model]
            [masques.view.utils :as view-utils]
            [seesaw.border :as seesaw-border]
            [seesaw.core :as seesaw-core]
            [seesaw.event :as seesaw-event]
            [seesaw.layout :as layout]
            [seesaw.mig :as seesaw-mig])
  (:import [java.awt Color]
           [javax.swing JLabel ImageIcon SwingConstants]))

(def background-color (Color/GRAY))
(def search-background-color (Color/LIGHT_GRAY))

(def logout-color view-utils/link-color)
(def logout-font (view-utils/create-link-font 18))

(def id "tool-bar")
(def icons-panel-id "icons-panel")
(def button-panel-name "panel-name")
(def button-panel-font (view-utils/create-link-font 14))
(def icons-button-group (seesaw-core/button-group))

(def logout-button-id :logout-button)

(defn create-masques-icon []
  (JLabel.
    (ImageIcon.
      (ClassLoader/getSystemResource "logo_for_dark_backgrounds_small.png"))))

(defn create-icons-bar []
  (seesaw-core/scrollable
    (seesaw-core/horizontal-panel
      :id icons-panel-id :items [] :background background-color :border 3)

    :border 0))

(defn logout []
  (seesaw-core/flow-panel
    :items [(seesaw-core/label
              :id :hello-label
              :text (term/hello-user
                      (profile-model/alias (profile-model/current-user)))
              :foreground logout-color
              :font logout-font)
            (view-utils/create-link-button
              :id logout-button-id
              :text (term/logout)
              :foreground logout-color
              :font logout-font
              :background background-color)]
    :background background-color))

(defn search-text-panel []
  (seesaw-core/flow-panel
    :items [(seesaw-core/label :id :search-label :text (term/search) :background search-background-color)
            (seesaw-core/text :id :search-text :columns 10 :background search-background-color)]
    :background search-background-color))

(defn search-radios-panel []
  (let [share-button-group (seesaw-core/button-group)]
    (seesaw-core/flow-panel
      :items [(seesaw-core/radio :id :search-shares-radio :text (term/shares) :background search-background-color :group share-button-group)
              (seesaw-core/radio :id :search-friends-radio :text (term/friends) :background search-background-color :group share-button-group)]

      :background search-background-color
      :border (seesaw-border/line-border :top 1))))

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

    :hgap 0
    :background background-color))

(defn create []
  (seesaw-core/border-panel
    :id id

    :west (create-masques-icon)
    :center (create-icons-bar)
    :east (create-global-actions-panel)

    :hgap 15
    :background background-color
    :border (seesaw-border/compound-border
              (seesaw-border/empty-border :thickness 5)
              (seesaw-border/line-border :thickness 1 :color (Color/LIGHT_GRAY))
              (seesaw-border/line-border :thickness 1 :color background-color))
    :preferred-size [900 :by 115]))

(defn create-icon-button-id [panel]
  (str "icon-" (panel-protocol/find-panel-name panel)))

(defn create-icon-button
  "Creates the icon button for the given panel. If the panel does not have an icon, then the text of the button is set
to the panel's name."
  [panel panel-button-listener]
  (let [icon (panel-protocol/icon panel)
        button (seesaw-core/radio
                 :id (create-icon-button-id panel)
                 :text (panel-protocol/display-text panel)
                 :icon icon
                 :border 5
                 :background background-color
                 :font button-panel-font
                 :foreground view-utils/link-color
                 :group icons-button-group)]
    (.setVerticalTextPosition button SwingConstants/BOTTOM)
    (.setHorizontalTextPosition button SwingConstants/CENTER)
    (seesaw-core/listen button :state-changed
            (fn [e] (seesaw-core/config! button :foreground 
                             (if (.isSelected button)
                               Color/WHITE
                               view-utils/link-color))))
    (view-utils/add-mouse-over-background-change
      :widget button :background background-color :hover-color :darkgray
      :pressed-color :lightgray)
    (view-utils/save-component-property button button-panel-name
                                        (panel-protocol/panel-name panel))
    (seesaw-event/listen button :action-performed panel-button-listener)
    button))

(defn find-tool-bar
  "Returns the tool-bar panel from the given frame."
  [frame]
  (view-utils/find-component frame id))

(defn find-icons-panel
  "Finds the panel which holds the icons on the main frame."
  [tool-bar]
  (view-utils/find-component tool-bar icons-panel-id))

(defn find-icon-button
  "Finds the button for the given panel in the given tool bar."
  [tool-bar panel]
  (view-utils/find-component
    (find-icons-panel tool-bar) (create-icon-button-id panel)))

(defn find-logout-button
  "Finds the button for the given panel in the given tool bar."
  [tool-bar]
  (view-utils/find-component tool-bar logout-button-id))

(defn add-icon
  "Adds the given panel as an icon on the tool-bar."
  [tool-bar panel panel-button-listener]
  (let [icons-panel (find-icons-panel tool-bar)
        current-icons (seesaw-core/config icons-panel :items)]
    (seesaw-core/config! icons-panel
      :items (concat current-icons
                     [[:fill-h 15]
                      (create-icon-button panel panel-button-listener)]))))

(defn select-icon-button
  "Selects the button for the given panel in the given tool bar."
  [tool-bar panel]
  (seesaw-core/config! (find-icon-button tool-bar panel) :selected? true))

(defn logout-listener
  "Closes the main frame and exits."
  [event]
  (let [frame (view-utils/top-level-ancestor (seesaw-core/to-widget event))]
    (.hide frame)
    (.dispose frame)
    (System/exit 0)))

(defn attach-logout-listener
  "Attaches the logout listener to the logout button in the given tool bar."
  [tool-bar]
  (view-utils/add-action-listener-to-button
    (find-logout-button tool-bar) logout-listener))