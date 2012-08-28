(ns masques.view.group.add
  (:require [clj-internationalization.term :as term]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(defn create-group-text []
  (seesaw-core/scrollable (seesaw-core/text :id :group-text)))

(defn create-center-panel []
  (seesaw-core/border-panel
      :vgap 3
      :north (term/group)
      :center (create-group-text)))

(defn create-button-panel []
  (seesaw-core/border-panel
      :border 5
      :hgap 5
      :east (seesaw-core/horizontal-panel :items 
              [ (seesaw-core/button :id :add-button :text (term/add))
                [:fill-h 3]
                (seesaw-core/button :id :cancel-button :text (term/cancel)) ])))

(defn create-content []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :center (create-center-panel)
      :south (create-button-panel)))

(defn create [parent-component]
  (view-utils/center-window-on parent-component
    (seesaw-core/frame
      :title (term/add-group)
      :content (create-content)
      :on-close :dispose
      :visible? false)))

(defn find-add-button
  "Returns the add button."
  [add-group-panel]
  (seesaw-core/select add-group-panel ["#add-button"]))

(defn find-cancel-button
  "Returns the cancel button."
  [add-group-panel]
  (seesaw-core/select add-group-panel ["#cancel-button"]))

(defn find-group-text
  "Returns the group text field."
  [add-group-panel]
  (seesaw-core/select add-group-panel ["#group-text"]))

(defn group-text
  "If given both the add-group-panel and text, then this function sets the group text. If this function is only given
the add-group-panel, then this function returns the text in the group text field." 
  ([add-group-panel]
    (let [group-text-str (.getText (find-group-text add-group-panel))]
      (when (not-empty group-text-str)
        group-text-str)))
  ([add-group-panel text]
    (.setText (find-group-text add-group-panel) text)
    text))

(defn click-add-button
  "Clicks the add button."
  [add-group-panel]
  (.doClick (find-add-button add-group-panel)))

(defn click-cancel-button
  "Clicks the cancel button."
  [add-group-panel]
  (.doClick (find-cancel-button add-group-panel)))