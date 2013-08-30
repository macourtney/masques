(ns masques.view.group.panel
  (:require [clj-internationalization.term :as term]
            [masques.view.utils :as view-utils]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core]))

(def group-background-color (seesaw-color/color 238 238 238))
(def group-button-font { :name "DIALOG" :style :plain :size 10 })

(defn create-button [id text]
  (seesaw-core/button :id id :text text :border 0 :font group-button-font :background group-background-color))

(defn create-filter []
  (seesaw-core/border-panel
    :north
      (seesaw-core/vertical-panel
        :items [(seesaw-core/horizontal-panel
                    :items
                      [(seesaw-core/combobox :id :group-combobox :preferred-size [250 :by 25])
                       [:fill-h 3]
                       (create-button :edit-group-button (term/edit))
                       [:fill-h 3]
                       (create-button :delete-group-button (term/delete))])
                (seesaw-core/border-panel :west (create-button :create-new-group-button (term/create-new-group)))])))

(defn create-header []
  (seesaw-core/border-panel
    :west (create-filter)
    :east (seesaw-core/label :text (term/groups) :foreground "#380B61" :font { :size 48 })))

(defn create-members-header []
  (seesaw-core/border-panel
    :west (seesaw-core/border-panel :south (create-button :add-member-button (term/add-member)))
    :east (seesaw-core/button :id :filter-members-button :text (term/filter))))

(defn create-members-table []
  (seesaw-core/scrollable
    (seesaw-core/table :model [:columns [:name :added-at :view :remove]])))

(defn create-members []
  (seesaw-core/border-panel
    :north (create-members-header)
    :center (create-members-table)

    :vgap 5
    :border 11))

(defn create-shares []
  (seesaw-core/flow-panel :items ["Shares Tab"]))

(defn create-body []
  (seesaw-core/tabbed-panel
    :tabs [{ :title (term/members) :content (create-members) }
           { :title (term/shares) :content (create-shares) }]))

(defn create []
  (seesaw-core/border-panel
    :id "group-panel"

    :north (create-header)
    :center (create-body)

    :border 11))