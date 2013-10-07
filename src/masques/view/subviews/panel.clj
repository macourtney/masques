(ns masques.view.subviews.panel
  (:require [clj-internationalization.term :as term]
            [seesaw.border :as border]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]
           [javax.swing JLabel ImageIcon]))

(def background-color (seesaw-color/color 238 238 238))
(def button-font { :name "DIALOG" :style :plain :size 10 })

(defn create-panel-label
  "Creates a label for use as the title of a panel."
  [text]
  (seesaw-core/label :text text :foreground "#380B61" :font { :size 48 }))

(defn create-button [id text]
  (seesaw-core/button :id id :text text :border 0 :font button-font :background background-color))