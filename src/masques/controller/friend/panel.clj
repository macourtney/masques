(ns masques.controller.friend.panel
  (:require [clj-internationalization.term :as term]
            [masques.controller.main.panel-protocol :as panel-protocol]
            [masques.view.friend.panel :as panel-view])
  (:import [masques.controller.main.panel_protocol PanelProtocol]
           [javax.swing ImageIcon]))



(deftype FriendPanel []
  PanelProtocol
  (panel-name [this] "Friends")

  (create-view [this] (panel-view/create this))

  (icon [this] (ImageIcon. (ClassLoader/getSystemResource "friends.png")))
  
  (display-text [this] (term/friends))

  (init [this view show-panel-fn] (panel-view/initialize view show-panel-fn))

  (show [this view args] (panel-view/show view args))

  (hide [this view])
  
  (destroy [this view]
    (panel-view/destroy view)))

(defn create []
  (FriendPanel.))