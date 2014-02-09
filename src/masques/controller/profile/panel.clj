(ns masques.controller.profile.panel
  (:require [masques.controller.actions.utils :as action-utils]
            [masques.controller.main.panel-protocol :as panel-protocol]
            [masques.model.profile :as profile-model]
            [masques.view.profile.panel :as panel-view]
            [seesaw.core :as seesaw-core]
            [seesaw.dev :as seesaw-dev]
            [seesaw.event :as seesaw-event])
  (:import [masques.controller.main.panel_protocol PanelProtocol]
           [javax.swing ImageIcon]))

(def panel-name-str "Profile")

(defn create-save-profile-listener [panel]
  (fn [event]
    (let [page-text (panel-view/profile-body-text panel)
          alias-text (seesaw-core/text (panel-view/find-profile-name-text panel))]
      (profile-model/save {:page page-text :alias alias-text}))))

(defn attach-save-profile-listener [panel]
  (let [save-button (panel-view/find-profile-save-button panel)
        listener (create-save-profile-listener panel)]
    (action-utils/attach-listener save-button listener)))

(defn attach-listeners [panel]
  (attach-save-profile-listener panel)
  panel)

(defn create-profile-panel []
  (attach-listeners (panel-view/create)))

(defn fill-profile
  "Fills the given profile panel view with data from the currently logged in user."
  [view]
  (let [current-user (profile-model/current-user)]
    (panel-view/fill-panel view current-user)))
           
(deftype ProfilePanel []
  PanelProtocol
  (panel-name [this] panel-name-str)

  (create-view [this] (create-profile-panel))

  (icon [this] (ImageIcon. (ClassLoader/getSystemResource "profile.png")))

  (init [this view])

  (show [this view args]
    (fill-profile view))

  (hide [this view]))

(defn create []
  (ProfilePanel.))