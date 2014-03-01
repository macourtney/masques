(ns masques.controller.main.main-frame
  (:require [clojure.tools.logging :as logging]
            [masques.controller.friend.panel :as friend-panel]
            [masques.controller.group.panel :as group-panel]
            [masques.controller.main.display-panel :as display-panel]
            [masques.controller.profile.panel :as profile-panel]
            [masques.controller.stream.panel :as stream-panel]
            [masques.controller.utils :as controller-utils]
            [masques.view.main.main-frame :as view-main-frame]
            [masques.view.main.tool-bar :as tool-bar-view]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(declare show-panel)

(defn show-panel
  "Shows the given panel (or panel id) passing along the args to the panel."
  [main-frame panel & args]
  (view-main-frame/show-panel main-frame panel args))

(defn panel-button-listener [event]
  (let [button (seesaw-core/to-widget event)
        main-frame (view-utils/top-level-ancestor button)]
    (show-panel main-frame
      (view-utils/retrieve-component-property
        button tool-bar-view/button-panel-name))))

(defn add-panels
  "Adds the given panel to the main frame."
  [main-frame & panels]
  (doseq [panel panels]
    (view-main-frame/add-panel main-frame panel panel-button-listener
                               (partial show-panel main-frame)))
  main-frame)

(defn load-default-panels
  "Adds the default panels to the main frame."
  [main-frame]
  (let [stream-panel (stream-panel/create)]
    (add-panels main-frame stream-panel (group-panel/create) (friend-panel/create) (profile-panel/create))
    (show-panel main-frame stream-panel)
    main-frame))

(defn show
  "Creates and shows the main frame."
  []
  (let [main-frame (view-main-frame/create)]
    (display-panel/init (view-main-frame/find-display-panel main-frame))
    (controller-utils/show (load-default-panels main-frame))))

