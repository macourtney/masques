(ns masques.view.group.panel
  (:require [clj-internationalization.term :as term]
            [masques.model.grouping :as grouping-model]
            [masques.view.group.all-groups-combobox-model
              :as all-groups-combobox-model]
            [masques.view.utils :as view-utils]
            [masques.view.utils.list-renderer :as list-renderer]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core]))

(def group-button-font { :name "DIALOG" :style :plain :size 10 })

(defn create-under-construction-button [id text]
  (view-utils/create-under-construction-link-button
    :id id
    :text text
    :font group-button-font))

(defn create-group-combobox
  "Creates the combobox which displays all of the groups in the system."
  []
  (let [group-combobox (seesaw-core/combobox
                         :id :group-combobox
                         :preferred-size [250 :by 25]
                         :model (all-groups-combobox-model/create))]
    (seesaw-core/selection! group-combobox
                            (select-keys
                              (grouping-model/find-grouping
                                (grouping-model/find-everyone-id))
                              [:id grouping-model/display-key]))
    (list-renderer/set-renderer
      group-combobox 
      (list-renderer/create-record-text-cell-renderer
        grouping-model/display-key))
    group-combobox))

(defn create-filter []
  (seesaw-core/border-panel
    :north
      (seesaw-core/vertical-panel
        :items [(seesaw-core/horizontal-panel
                    :items
                      [(create-group-combobox)
                       [:fill-h 3]
                       (create-under-construction-button
                         :edit-group-button (term/edit))
                       [:fill-h 3]
                       (create-under-construction-button
                         :delete-group-button (term/delete))])
                (seesaw-core/border-panel
                  :west (create-under-construction-button
                          :create-new-group-button (term/create-new-group)))])))

(defn create-header []
  (seesaw-core/border-panel
    :west (create-filter)
    :east (seesaw-core/label :text (term/groups) :foreground "#380B61"
                             :font { :size 48 })))

(defn create-members-header []
  (seesaw-core/border-panel
    :west (seesaw-core/border-panel
            :south (create-under-construction-button
                     :add-member-button (term/add-member)))
    :east (create-under-construction-button
            :filter-members-button (term/filter))))

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