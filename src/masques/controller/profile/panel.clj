(ns masques.controller.profile.panel
  (:require [masques.controller.main.panel-protocol :as panel-protocol]
            [masques.view.profile.panel :as panel-view]
            [seesaw.core :as seesaw-core])
  (:import [masques.controller.main.panel_protocol PanelProtocol]
           [javax.swing ImageIcon]))

(deftype ProfilePanel []
  PanelProtocol
  (panel-name [this] "Profile")

  (create-view [this] (panel-view/create))

  (icon [this] (ImageIcon. (ClassLoader/getSystemResource "profile.png")))

  (init [this view])

  (show [this view args])

  (hide [this view]))

(defn create []
  (ProfilePanel.))