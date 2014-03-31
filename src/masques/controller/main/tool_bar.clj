(ns masques.controller.main.tool-bar
  (:require [clojure.tools.logging :as logging]
            [masques.view.main.tool-bar :as tool-bar-view]))

(defn init
  "Initializes the tool bar by attaching all listeners to buttons."
  [tool-bar]
  (tool-bar-view/attach-logout-listener tool-bar))