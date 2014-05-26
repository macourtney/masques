(ns masques.view.group.panel
  (:require [clj-internationalization.term :as term]
            [clojure.tools.logging :as logging]
            [masques.model.base :as model-base]
            [masques.model.grouping :as grouping-model]
            [masques.view.group.all-groups-combobox-model
              :as all-groups-combobox-model]
            [masques.view.utils :as view-utils]
            [masques.view.utils.korma-combobox-model :as korma-combobox-model]
            [masques.view.utils.list-renderer :as list-renderer]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core]))

(def group-panel-id :group-panel)

(def group-button-font { :name "DIALOG" :style :plain :size 10 })

(def group-combobox-id :group-combobox)

(def group-manager-panel-id :group-manager-panel)
(def group-edit-panel-id :group-edit-panel)

(def edit-group-button-id :edit-group-button)
(def edit-group-button-listener-key :edit-group-button-listener)

(def delete-group-button-id :delete-group-button)
(def delete-group-button-listener-key :delete-group-button-listener)

(def create-new-group-button-id :create-new-group-button)
(def create-new-group-button-listener-key :create-new-group-button-listener)

(def group-editor-text-id :group-editor-text)

(def cancel-edit-button-id :cancel-edit-button)
(def cancel-edit-button-listener-key :cancel-edit-button-listener)

(def save-edit-button-id :save-edit-button)
(def save-edit-button-listener-key :save-edit-button-listener)

(def group-filter-card-id :group-filter-card)
(def group-editor-card-id :group-editor-card)

(def group-editor-id-key :group-editor-id-type)
(def group-editor-type-key :group-editor-type)
(def create-group :create-group)
(def edit-group :edit-group)

(defn create-under-construction-button [id text]
  (view-utils/create-under-construction-link-button
    :id id
    :text text
    :font group-button-font))

(defn create-button [id text]
  (view-utils/create-link-button
    :id id
    :text text
    :font group-button-font))

(defn create-group-combobox
  "Creates the combobox which displays all of the groups in the system."
  []
  (let [group-combobox (seesaw-core/combobox
                         :id group-combobox-id
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
                       (create-button edit-group-button-id (term/edit))
                       [:fill-h 3]
                       (create-button delete-group-button-id (term/delete))])
                (seesaw-core/border-panel
                  :west (create-button
                          create-new-group-button-id
                          (term/create-new-group)))])))

(defn create-group-editor []
  (seesaw-core/horizontal-panel
    :id group-edit-panel-id
    :items [(seesaw-core/vertical-panel
              :items [[:fill-v 15]
                      (seesaw-core/text :id group-editor-text-id)
                      [:fill-v 15]])
            [:fill-h 3]
            (create-button save-edit-button-id (term/save))
            [:fill-h 3]
            (create-button cancel-edit-button-id (term/cancel))]))

(defn create-group-manager []
  (seesaw-core/card-panel
    :id group-manager-panel-id
    :items [[(create-filter) group-filter-card-id]
            [(create-group-editor) group-editor-card-id]]))

(defn create-header []
  (seesaw-core/border-panel
    :west (create-group-manager)
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
    :id group-panel-id

    :north (create-header)
    :center (create-body)

    :border 11))

(defn find-group-panel
  "Finds the group combobox in the given group panel."
  [view]
  (view-utils/find-component view group-panel-id))

(defn find-group-combobox
  "Finds the group combobox in the given group panel."
  [panel]
  (view-utils/find-component panel group-combobox-id))

(defn destroy
  "Should be called right before the panel is destroyed."
  [panel]
  (korma-combobox-model/destroy-model (find-group-combobox panel)))

(defn find-create-new-group-button
  "Finds the create new group button in the given view."
  [view]
  (view-utils/find-component view create-new-group-button-id))

(defn find-cancel-edit-button
  "Finds the cancel edit button in the given view."
  [view]
  (view-utils/find-component view cancel-edit-button-id))

(defn find-save-edit-button
  "Finds the save edit button in the given view."
  [view]
  (view-utils/find-component view save-edit-button-id))

(defn find-delete-group-button
  "Finds the delete group button in the given view."
  [view]
  (view-utils/find-component view delete-group-button-id))

(defn find-edit-group-button
  "Finds the edit group button in the given view."
  [view]
  (view-utils/find-component view edit-group-button-id))

(defn find-group-manager-panel
  "Finds the group manager panel."
  [view]
  (view-utils/find-component view group-manager-panel-id))

(defn find-group-edit-panel
  "Finds the group editor panel."
  [view]
  (view-utils/find-component view group-edit-panel-id))

(defn find-group-editor-text
  "Finds the group editor text field."
  [view]
  (view-utils/find-component view group-editor-text-id))

(defn selected-group
  "Returns the selected group in the group combobox."
  [view]
  (seesaw-core/selection (find-group-combobox view)))

(defn show-group-editor-panel
  "Shows the group editor panel."
  [view]
  (seesaw-core/show-card!
    (find-group-manager-panel view) group-editor-card-id)
  view)

(defn show-group-filter-panel
  "Shows the group filter panel."
  [view]
  (seesaw-core/show-card!
    (find-group-manager-panel view) group-filter-card-id)
  view)

(defn create-group-listener
  "Shows the create group panel."
  [event]
  (let [group-edit-panel (find-group-edit-panel
                           (show-group-editor-panel
                             (find-group-manager-panel
                               (view-utils/top-level-ancestor event))))]
    (view-utils/save-component-property
      group-edit-panel group-editor-type-key create-group)
    (seesaw-core/text! (find-group-editor-text group-edit-panel) nil)))

(defn attach-create-group-listener
  "Attaches the create group listener to the create group button in the given
view."
  [view]
  (view-utils/add-action-listener-to-button
    (find-create-new-group-button view) create-group-listener
    create-new-group-button-listener-key))

(defn cancel-edit-listener
  "Shows the create group panel."
  [event]
  (view-utils/remove-component-property
    (find-group-edit-panel
      (show-group-filter-panel
        (find-group-manager-panel
          (view-utils/top-level-ancestor event))))
    group-editor-type-key))

(defn attach-cancel-edit-listener
  "Attaches the cancel edit listener to the cancel edit button in the given
view."
  [view]
  (view-utils/add-action-listener-to-button
    (find-cancel-edit-button view) cancel-edit-listener
    cancel-edit-button-listener-key))

(defn create-new-group
  "Creates a new group from the text in the given group edit panel."
  [group-edit-panel]
  (let [group-name (seesaw-core/text
                     (find-group-editor-text group-edit-panel))]
    (when (not-empty group-name)
      (grouping-model/create-user-group group-name))))

(defn edit-selected-group
  "Updates the name of a group given the text and the group to edit saved in the
given group edit panel."
  [group-edit-panel]
  (let [group-id (view-utils/retrieve-component-property
                   group-edit-panel group-editor-id-key)
        new-display (seesaw-core/text
                      (find-group-editor-text group-edit-panel))]
    (when (not-empty new-display)
      (model-base/update-record
        model-base/grouping
        { model-base/id-key group-id
          grouping-model/display-key new-display }))))

(defn save-edit-listener
  "Saves or edits a group using the text in the group text editor panel."
  [event]
  (let [group-edit-panel (find-group-edit-panel
                              (find-group-panel
                                (view-utils/top-level-ancestor event)))]
    (when-let [editor-type (view-utils/retrieve-component-property
                             group-edit-panel group-editor-type-key)]
      (condp = editor-type
        create-group (create-new-group group-edit-panel)
        edit-group (edit-selected-group group-edit-panel)
        (throw (RuntimeException. (str "Unknown editor type: " editor-type))))))
  (cancel-edit-listener event))

(defn attach-save-edit-listener
  "Attaches the cancel edit listener to the cancel edit button in the given
view."
  [view]
  (view-utils/add-action-listener-to-button
    (find-save-edit-button view) save-edit-listener
    save-edit-button-listener-key))

(defn delete-group-listener
  "Deletes the group selected in the group combobox."
  [event]
  (let [group-panel (find-group-panel (view-utils/top-level-ancestor event))]
    (when-let [current-selected-group (selected-group group-panel)]
      (if (grouping-model/contains-any-profile? current-selected-group)
        (seesaw-core/alert group-panel (term/cannot-delete-group-with-friends))
        (when (= (term/delete)
               (seesaw-core/input
                 (term/are-you-sure-you-want-to-delete-group
                   (grouping-model/display current-selected-group))
                 :choices [(term/delete) (term/do-not-delete)]))
          (grouping-model/delete-grouping current-selected-group))))))

(defn attach-delete-group-listener
  "Attaches the delete group listener to the delete group button in the given
view."
  [view]
  (view-utils/add-action-listener-to-button
    (find-delete-group-button view) delete-group-listener
    delete-group-button-listener-key))

(defn edit-group-listener
  "Edit the group selected in the group combobox."
  [event]
  (let [group-panel (find-group-panel (view-utils/top-level-ancestor event))]
    (when-let [current-selected-group (selected-group group-panel)]
      (let [group-edit-panel (find-group-edit-panel
                               (show-group-editor-panel
                                 (find-group-manager-panel
                                   (view-utils/top-level-ancestor event))))]
        (view-utils/save-component-property
          group-edit-panel group-editor-type-key edit-group)
        (view-utils/save-component-property
          group-edit-panel group-editor-id-key
          (model-base/id current-selected-group))
        (seesaw-core/text! (find-group-editor-text group-panel)
                           (grouping-model/display current-selected-group))))))

(defn attach-edit-group-listener
  "Attaches the edit group listener to the edit group button in the given view."
  [view]
  (view-utils/add-action-listener-to-button
    (find-edit-group-button view) edit-group-listener
    edit-group-button-listener-key))

(defn initialize
  "Called when the panel is created to initialize the view by attaching
listeners and loading initial data."
  [view show-panel-fn]
  (attach-create-group-listener view)
  (attach-cancel-edit-listener view)
  (attach-save-edit-listener view)
  (attach-delete-group-listener view)
  (attach-edit-group-listener view))