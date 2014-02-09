(ns masques.view.utils
  (:require [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core]))

(def link-color "#FFAA00")

(defn create-link-font [size]
  { :name "DIALOG" :style :bold :size size })

(defn center-window-on [parent window]
  (seesaw-core/pack! window)
  (.setLocationRelativeTo window parent)
  window)

(defn center-window [window]
  (center-window-on nil window))

(defn find-component [parent-component id]
  (seesaw-core/select parent-component [id]))

(defn save-component-property [component key value]
  (.putClientProperty component key value)
  value)

(defn retrieve-component-property [component key]
  (.getClientProperty component key))

(defn remove-component-property [component key]
  (let [value (retrieve-component-property component key)]
    (save-component-property component key nil)
    value))
    
(defn top-level-ancestor
"Returns the top-level ancestor of the given component (either the containing Window or Applet), or null if the component is null or has not been added to any container."
  [component]
  (when component
    (.getTopLevelAncestor component)))