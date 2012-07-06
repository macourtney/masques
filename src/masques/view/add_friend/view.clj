(ns masques.view.add-friend.view
  (:require [clj-internationalization.core :as clj-i18n]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(defn create-friend-text []
  (let [text-area (seesaw-core/text
                    :id :friend-text
                    :multi-line? true
                    :preferred-size [400 :by 60])]
    (.setLineWrap text-area true)
    (seesaw-core/scrollable text-area)))

(defn create-center-panel []
  (seesaw-core/border-panel
      :vgap 3
      :north (clj-i18n/friend)
      :center (create-friend-text)))

(defn create-button-panel []
  (seesaw-core/border-panel
      :border 5
      :hgap 5
      :west (seesaw-core/horizontal-panel :items 
              [ (seesaw-core/button :id :import-button :text (clj-i18n/import))
                [:fill-h 3]
                (seesaw-core/button :id :paste-button :text (clj-i18n/paste))
                [:fill-h 3] ])
      :east (seesaw-core/horizontal-panel :items 
              [ (seesaw-core/button :id :add-button :text (clj-i18n/add))
                [:fill-h 3]
                (seesaw-core/button :id :cancel-button :text (clj-i18n/cancel)) ])))

(defn create-content []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :center (create-center-panel)
      :south (create-button-panel)))

(defn create [main-frame]
  (view-utils/center-window-on main-frame
    (seesaw-core/frame
      :title (clj-i18n/add-friend)
      :content (create-content)
      :on-close :dispose
      :visible? false)))

(defn find-paste-button [add-friend-panel]
  (seesaw-core/select add-friend-panel ["#paste-button"]))

(defn find-friend-text [add-friend-panel]
  (seesaw-core/select add-friend-panel ["#friend-text"]))

(defn friend-text
  ([add-friend-panel]
    (let [friend-text-str (.getText (find-friend-text add-friend-panel))]
      (when (not-empty friend-text-str)
        friend-text-str)))
  ([add-friend-panel text]
    (.setText (find-friend-text add-friend-panel) text)))

(defn click-paste-button
  "Clicks the paste button."
  [main-frame]
  (.doClick (find-paste-button main-frame)))