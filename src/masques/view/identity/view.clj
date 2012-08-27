(ns masques.view.identity.view
  (:require [clj-internationalization.term :as term]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(defn create-label-value-pair-panel [text label-key]
  (seesaw-core/horizontal-panel
      :items [ (seesaw-core/border-panel :size [150 :by 15] :east (seesaw-core/label :text text))
               [:fill-h 3]
               (seesaw-core/border-panel :size [200 :by 15]
                 :west (seesaw-core/label :id label-key :text "data" :font { :style :plain }))]))

(defn create-name-panel []
  (create-label-value-pair-panel (term/name) :name-label))

(defn create-public-key-panel []
  (create-label-value-pair-panel (term/public-key) :public-key-label))

(defn create-algorithm-panel []
  (create-label-value-pair-panel (term/algorithm) :public-key-algorithm-label))

(defn create-is-online-panel []
  (create-label-value-pair-panel (term/is-online) :is-online-label))

(defn create-data-panel []
  (seesaw-core/vertical-panel
    :items [ (create-name-panel)
             [:fill-v 3]
             (create-public-key-panel)
             [:fill-v 3]
             (create-algorithm-panel)
             [:fill-v 3]
             (create-is-online-panel)]))

(defn create-center-panel []
  (seesaw-core/border-panel
      :border 5
      :hgap 5
      :north (seesaw-core/border-panel :border 0 :west (create-data-panel))))

(defn create-button-panel []
  (seesaw-core/border-panel
      :border 5
      :hgap 5
      :east (seesaw-core/horizontal-panel :items 
              [ (seesaw-core/button :id :cancel-button :text (term/done)) ])))

(defn create-content []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :center (create-center-panel)
      :south (create-button-panel)))

(defn create [main-frame]
  (view-utils/center-window-on main-frame
    (seesaw-core/frame
      :title (term/identity)
      :content (create-content)
      :on-close :dispose
      :visible? false)))