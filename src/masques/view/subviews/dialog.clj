(ns masques.view.subviews.dialog
  (:require [clj-internationalization.term :as term]
            [seesaw.border :as border]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]
           [javax.swing JLabel ImageIcon]))

(defn create-header [label-text]
  (seesaw-core/border-panel
    :west (JLabel. (ImageIcon. (ClassLoader/getSystemResource "logo_for_light_backgrounds_small.png")))
    :east (seesaw-core/label :text label-text :foreground "#380B61" :font { :size 48 })
    :border (border/empty-border :top 10 :left 10 :right 10)
    ))
    
(defn create-footer []
  (seesaw-core/border-panel
    :east (seesaw-core/label :text (term/masques-version) :foreground (Color/WHITE))
    :background (Color/GRAY)

    :border 5))

(defn create-content [label-text content preferred-size]
  (seesaw-core/border-panel
    :id :content-panel
    :north (create-header label-text)
    :center content
    :south (create-footer)
    
    :preferred-size preferred-size))