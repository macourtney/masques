(ns masques.controller.profile.panel
  (:require [masques.controller.main.panel-protocol :as panel-protocol]
            [masques.model.profile :as profile-model]
            [masques.view.profile.panel :as panel-view]
            [seesaw.core :as seesaw-core])
  (:import [masques.controller.main.panel_protocol PanelProtocol]
           [javax.swing ImageIcon]))

(def panel-name-str "Profile")

(defn fill-profile
  "Fills the given profile panel view with data from the currently logged in user."
  [view]
  (let [current-user (profile-model/current-user)]
    (panel-view/fill-panel view current-user)))
           
(deftype ProfilePanel []
  PanelProtocol
  (panel-name [this] panel-name-str)

  (create-view [this] (panel-view/create))

  (icon [this] (ImageIcon. (ClassLoader/getSystemResource "profile.png")))

  (init [this view])

  (show [this view args]
    (fill-profile view))

  (hide [this view]))

(defn create []
  (ProfilePanel.))