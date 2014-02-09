(ns masques.controller.main.panel-protocol
  (:require [clojure.tools.logging :as logging]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JMenuItem JMenu]))

(defprotocol PanelProtocol
  "A protocol for displaying and updating panels added to the main window."
  (panel-name [this] "Returns the name of this panel.")

  (create-view [this] "Creates the view to be placed in the display area of the main window.")

  (icon [this] "Returns the icon for this panel.")
  
  (display-text [this] "Returns the text representation of this panel. Used in the tool bar.")

  (init [this view] "Called when the panel is added.")

  (show [this view args] "Called right before the panel is shown.")

  (hide [this view] "Called right after the panel is hidden."))

(defn find-panel-name
  "Returns the name of the given panel. If the given panel does not satisfy PanelProtocol, then this function simply
returns panel."
  [panel]
  (if (satisfies? PanelProtocol panel)
    (panel-name panel)
    panel))