(ns masques.view.main.display-panel
  (:require [seesaw.core :as seesaw-core]))

(defn create-card-panel []
  (seesaw-core/flow-panel :items ["Display Panel"]))

(defn create []
  (seesaw-core/border-panel
    :center (create-card-panel)))