(ns masques.controller.main.main-frame
  (:require [clojure.tools.logging :as logging]
            [masques.controller.group.panel :as group-panel]
            [masques.controller.main.display-panel :as display-panel]
            [masques.controller.stream.panel :as stream-panel]
            [masques.controller.utils :as controller-utils]
            [masques.view.main.main-frame :as view-main-frame]))

(defn add-panels
  "Adds the given panel to the main frame."
  [main-frame & panels]
  (doseq [panel panels]
    (view-main-frame/add-panel main-frame panel))
  main-frame)

(defn load-default-panels
  "Adds the default panels to the main frame."
  [main-frame]
  (add-panels main-frame (group-panel/create) (stream-panel/create)))

(defn show
  "Creates and shows the main frame."
  []
  (let [main-frame (view-main-frame/create)]
    (display-panel/init (view-main-frame/find-display-panel main-frame))
    (controller-utils/show (load-default-panels main-frame))))

(defn show-panel
  "Shows the given panel (or panel id) passing along the args to the panel."
  [main-frame panel & args]
  (view-main-frame/show-panel main-frame panel args))