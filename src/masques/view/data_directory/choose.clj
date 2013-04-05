(ns masques.view.data-directory.choose
  (:require [clj-internationalization.term :as term]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]
           [javax.swing JLabel ImageIcon]))

(defn create-header []
  (seesaw-core/border-panel
    :west (JLabel. (ImageIcon. "resources/masques.ico"))
    :east (seesaw-core/label :text (term/file-storage))))

(defn create-directory-chooser-panel []
  (seesaw-core/vertical-panel
    :items [(create-header)
            [:fill-v 5]
            (seesaw-core/label :text (term/directory-to-store-masques-data))
            [:fill-v 3]
            (seesaw-core/text :id :data-directory-text :editable? false)]))

(defn create-button-panel []
  (seesaw-core/vertical-panel
    :items [(seesaw-core/horizontal-panel
              :items
                [(seesaw-core/button :id :cancel-button :text (term/cancel))
                 [:fill-h 3]
                 (seesaw-core/button :id :data-directory-button :text (term/choose))])
            (seesaw-core/button :id :save-button :text (term/save-location))]))

(defn create-text-and-buttons []
  (seesaw-core/border-panel
    :center (seesaw-core/text :text (term/file-storage-description) :multi-line? true :editable? false :wrap-lines? true :rows 7)
    :east (create-button-panel)))

(defn create-footer []
  (seesaw-core/border-panel
    :east (seesaw-core/label :text (term/masques-version))
    :background (Color/DARK_GRAY)))

(defn create-content []
  (seesaw-core/border-panel
    :north (create-directory-chooser-panel)
    :center (create-text-and-buttons)
    :south (create-footer)
    ;:items [(create-directory-chooser-panel) [:fill-v 5] (create-button-panel)]
    ))

(defn create []
  (view-utils/center-window
    (seesaw-core/frame
      :title (term/choose-data-directory)
      :content (create-content)
      :visible? false)))