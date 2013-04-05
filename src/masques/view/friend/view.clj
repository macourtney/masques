(ns masques.view.friend.view
  (:refer-clojure :exclude [load])
  (:require [clj-internationalization.term :as term]
            [masques.view.subviews.profile-data :as profile-data]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(defn create-center-panel []
  (profile-data/create-profile-panel))

(defn create-button-panel []
  (seesaw-core/border-panel
      :border 5
      :hgap 5
      :east (seesaw-core/horizontal-panel :items 
              [ (seesaw-core/button :id :done-button :text (term/done)) ])))

(defn create-content []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :center (create-center-panel)
      :south (create-button-panel)))

(defn create [main-frame]
  (view-utils/center-window-on main-frame
    (seesaw-core/frame
      :title (term/view-friend)
      :content (create-content)
      :on-close :dispose
      :visible? false)))

(defn load [profile main-frame]
  (profile-data/set-data main-frame profile))

(defn scrape-profile [main-frame]
  (profile-data/scrape-data main-frame))

(defn find-done-button [main-frame]
  (seesaw-core/select main-frame ["#done-button"]))