(ns masques.controller.actions.utils
  (:require [clojure.tools.logging :as logging]
            [masques.controller.utils :as controller-utils]
            [seesaw.core :as seesaw-core]))

(defn attach-listener
  "Attaches the given listener to the given component or finds the component using the parent component and the component id."
  ([component listener]
    (seesaw-core/listen component :action listener))
  ([parent-component id listener]
    (attach-listener (controller-utils/find-component parent-component id) listener)
    parent-component))

(defn frame-listener [listener e]
  (listener (seesaw-core/to-frame e) e))

(defn attach-frame-listener
  "Attaches a frame listener to the given component or finds the component using the parent component and the component id."
  ([component listener]
    (attach-listener component #(frame-listener listener %)))
  ([parent-component id listener]
    (attach-listener parent-component id #(frame-listener listener %))))

(defn close-window
  ([frame e] (close-window frame))
  ([frame]
    (.hide frame)
    (.dispose frame)))

(defn attach-window-close-listener [parent-component id]
  (attach-frame-listener parent-component id close-window))

(defn close-window-and-exit [frame e]
  (close-window frame e)
  (System/exit 0))

(defn attach-window-close-and-exit-listener [parent-component id]
  (attach-frame-listener parent-component id close-window-and-exit))

(defn remove-all-action-listeners [button]
  (doseq [action-listener (.getActionListeners button)]
    (.removeActionListener button action-listener)))

(defn set-default-button [parent-frame button]
  (.setDefaultButton (.getRootPane parent-frame) button)
  parent-frame)