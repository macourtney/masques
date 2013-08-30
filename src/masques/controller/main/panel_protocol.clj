(ns masques.controller.main.panel-protocol
  (:require [clojure.tools.logging :as logging]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JMenuItem JMenu]))

(defprotocol PanelProtocol
  "A protocol for displaying and updating panels added to the main window."
  (panel-name [this] "Returns the name of this panel.")

  (panel [this] "Returns the panel to be placed in the display area of the main window.")

  (icon [this] "Returns the icon for this panel.")

  (init [this panel] "Initializes the panel.")

  (show [this panel] "Called right before the panel is shown.")

  (hide [this panel] "Called right after the panel is hidden."))