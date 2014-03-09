(ns masques.view.friend.test.panel
  (:require test.init
            [clj-internationalization.term :as term]
            [masques.controller.main.panel-protocol :as panel-protocol])
  (:use clojure.test
        masques.view.friend.panel)
  (:import [masques.controller.main.panel_protocol PanelProtocol]))

(deftype TestFriendPanel []
  PanelProtocol
  (panel-name [this] "Friends")

  (create-view [this] (create this))

  (icon [this])
  
  (display-text [this] (term/friends))

  (init [this view show-panel-fn] (initialize view show-panel-fn))

  (show [this view args] (show view args))

  (hide [this view]))

(deftest test-create
  (let [panel (TestFriendPanel.)]
    (panel-protocol/create-view panel)))