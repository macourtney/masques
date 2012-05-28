(ns masques.controller.main.main-menu-bar
  (:require [clojure.tools.logging :as logging]
            [masques.controller.actions.utils :as actions-utils]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JMenuItem JMenu]))

(defn attach-main-menu-actions [main-frame]
  (actions-utils/attach-window-close-and-exit-listener main-frame "#exit-menu-item"))

(defn init [main-frame]
  (attach-main-menu-actions main-frame))