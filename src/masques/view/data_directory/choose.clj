(ns masques.view.data-directory.choose
  (:require [clj-internationalization.term :as term]
            [masques.view.utils :as view-utils]
            [seesaw.border :as border]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]
           [javax.swing JLabel ImageIcon]))

(defn create-header []
  (seesaw-core/border-panel
    :west (JLabel. (ImageIcon. (ClassLoader/getSystemResource "masques.png")))
    :east (seesaw-core/label :text (term/file-storage) :foreground "#380B61" :font { :size 48 })))

(defn create-directory-chooser-panel []
  (seesaw-core/vertical-panel
    :items [(create-header)
            [:fill-v 5]
            (seesaw-core/border-panel
              :west (seesaw-core/label :text (term/directory-to-store-masques-data) :font { :style :plain }))
            [:fill-v 3]
            (seesaw-core/text :id :data-directory-text :editable? false)]

    :border 10))

(defn create-button-panel []
  (seesaw-core/border-panel
    :north
     (seesaw-core/vertical-panel
       :items [(seesaw-core/border-panel
                 :east (seesaw-core/horizontal-panel
                         :items
                         [(seesaw-core/button :id :cancel-button :text (term/cancel))
                           [:fill-h 3]
                           (seesaw-core/button :id :data-directory-button :text (term/choose))])

                 :border (border/empty-border :bottom 5))
               (seesaw-core/button :id :save-button :text (term/save-location))])))

(defn create-text-and-buttons []
  (seesaw-core/border-panel
    :center (seesaw-core/scrollable (seesaw-core/text :text (term/file-storage-description) :multi-line? true :editable? false :wrap-lines? true :rows 7))
    :east (create-button-panel)

    :border 10))

(defn create-footer []
  (seesaw-core/border-panel
    :east (seesaw-core/label :text (term/masques-version) :foreground (Color/WHITE))
    :background (Color/GRAY)

    :border 5))

(defn create-content []
  (seesaw-core/border-panel
    :id :content-panel
    :north (create-directory-chooser-panel)
    :center (create-text-and-buttons)
    :south (create-footer)

    :preferred-size [500 :by 300]))

(defn create []
  (view-utils/center-window
    (seesaw-core/frame
      :id :choose-data-directory-frame
      :title (term/choose-data-directory)
      :content (create-content)
      :visible? false)))

(defn data-directory-text [parent-component]
  (view-utils/find-component parent-component :#data-directory-text))

(defn cancel-button [parent-component]
  (view-utils/find-component parent-component :#cancel-button))

(defn choose-button [parent-component]
  (view-utils/find-component parent-component :#data-directory-button))

(defn save-button [parent-component]
  (view-utils/find-component parent-component :#save-button))

(defn content-panel [parent-component]
  (view-utils/find-component parent-component :#content-panel))

(defn add-cancel-action [parent-component action]
  (seesaw-core/listen (cancel-button parent-component) :action action))

(defn add-choose-action [parent-component action]
  (seesaw-core/listen (choose-button parent-component) :action action))

(defn add-save-action [parent-component action]
  (seesaw-core/listen (save-button parent-component) :action action))

(defn data-directory [parent-component]
  (.getText (data-directory-text parent-component)))

(defn click-save [parent-component]
  (.doClick (save-button parent-component)))