(ns masques.controller.stream.panel
  (:require [clj-internationalization.term :as term]
            [clojure.java.io :as java-io]
            [masques.controller.actions.utils :as actions-utils]
            [masques.controller.main.panel-protocol :as panel-protocol]
            [masques.controller.utils :as controller-utils]
            [masques.view.stream.panel :as panel-view]
            [seesaw.core :as seesaw-core])
  (:import [masques.controller.main.panel_protocol PanelProtocol]
           [javax.swing ImageIcon]))

(deftype StreamPanel []
  PanelProtocol
  (panel-name [this] "Stream")

  (create-view [this] (panel-view/create))

  (icon [this] (ImageIcon. (ClassLoader/getSystemResource "stream.png")))

  (display-text [this] (term/stream))

  (init [this view show-panel-fn])

  (show [this view args])

  (hide [this view])
  
  (destroy [this view]
    (panel-view/destroy view)))

(defn create []
  (StreamPanel.))