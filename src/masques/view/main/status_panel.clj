(ns masques.view.main.status-panel
  (:require [seesaw.core :as seesaw-core]))

(defn create-update-status []
  (seesaw-core/flow-panel :items ["update status"]))
  
(defn create-recent-shares []
  (seesaw-core/flow-panel :items ["recent shares"]))

(defn create-online-friends []
  (seesaw-core/flow-panel :items ["global actions bar"]))

(defn create []
  (seesaw-core/border-panel
    :north (create-update-status)
    :center (create-recent-shares)
    :south (create-online-friends)))