(ns masques.view.utils
  (:require [seesaw.core :as seesaw-core]))

(defn center-window-on [parent window]
  (seesaw-core/pack! window)
  (.setLocationRelativeTo window parent)
  window)

(defn center-window [window]
  (center-window-on nil window))

(defn find-component [parent-component id]
  (seesaw-core/select parent-component [id]))