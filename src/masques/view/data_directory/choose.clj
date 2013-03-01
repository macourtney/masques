(ns masques.view.data-directory.choose
  (:require [clj-internationalization.term :as term]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(defn create-directory-chooser-panel []
  (seesaw-core/horizontal-panel :items
    [ (term/data-directory)
      [:fill-h 3]
      (seesaw-core/text :id :data-directory-text :preferred-size [150 :by 25] :editable? false)
      [:fill-h 3]
      (seesaw-core/button :id :data-directory-button :text (term/choose))]))

(defn create-button-panel []
  (seesaw-core/border-panel :east
    (seesaw-core/horizontal-panel :items
      [(seesaw-core/button :id :save-button :text (term/save))
       [:fill-h 3]
       (seesaw-core/button :id :cancel-button :text (term/cancel))])))

(defn create-content []
  (seesaw-core/vertical-panel
    :border 5
    :items [(create-directory-chooser-panel) [:fill-v 5] (create-button-panel)]))

(defn create []
  (view-utils/center-window
    (seesaw-core/frame
      :title (term/choose-data-directory)
      :content (create-content)
      :visible? false)))