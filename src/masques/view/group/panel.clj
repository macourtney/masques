(ns masques.view.group.panel
  (:require [clj-internationalization.term :as term]
            [clojure.tools.logging :as logging]
            [masques.model.base :as model-base]
            [masques.model.friend-request :as friend-request]
            [masques.model.grouping :as grouping-model]
            [masques.model.grouping-profile :as grouping-profile]
            [masques.view.friend.selector :as friend-selector]
            [masques.view.group.all-groups-combobox-model
              :as all-groups-combobox-model]
            [masques.view.group.group-member-table-model
              :as group-member-table-model]
            [masques.view.utils :as view-utils]
            [masques.view.utils.button-table :as button-table]
            [masques.view.utils.button-table-cell-editor
              :as button-table-cell-editor]
            [masques.view.utils.korma-combobox-model :as korma-combobox-model]
            [masques.view.utils.korma-table-model :as korma-table-model]
            [masques.view.utils.list-renderer :as list-renderer]
            [masques.view.utils.table-renderer :as table-renderer]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core]))

(def group-panel-id :group-panel)

(def group-button-font { :name "DIALOG" :style :plain :size 10 })

(def group-combobox-id :group-combobox)
(def group-member-table-id :group-member-table)

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
(def group-display-card-id :group-display-card-id)

(def group-editor-id-key :group-editor-id-type)
(def group-editor-type-key :group-editor-type)
(def create-group :create-group)
(def edit-group :edit-group)

(def members-card-panel-id :members-card-panel-id)
(def group-display-id :group-display-id)

(def add-member-table-id :add-member-table-id)
(def add-member-button-id :add-member-button-id)
(def add-member-button-listener-key :add-member-button-listener-key)

(def main-panel-key :main-panel-key)
(def add-member-panel-key :add-member-panel-key)

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

(defn create-group-display []
  (seesaw-core/label
    :id group-display-id :text (term/groups) :foreground "#380B61"
    :font { :size 36 }))

(defn create-group-manager []
  (seesaw-core/card-panel
    :id group-manager-panel-id
    :items [[(create-filter) group-filter-card-id]
            [(create-group-editor) group-editor-card-id]
            [(create-group-display) group-display-card-id]]))

(defn create-header []
  (seesaw-core/border-panel
    :west (create-group-manager)
    :east (seesaw-core/label :text (term/groups) :foreground "#380B61"
                             :font { :size 48 })))

(defn create-members-header []
  (seesaw-core/border-panel
    :west (seesaw-core/border-panel
            :south (create-button add-member-button-id (term/add-member)))
    :east (create-under-construction-button
            :filter-members-button (term/filter))))

(defn create-members-table []
  (let [members-table (seesaw-core/table
                        :id group-member-table-id
                        :model [:columns [:name :added-at :view :remove]])]
    
    (seesaw-core/scrollable members-table)))

(defn create-members-table-panel []
  (seesaw-core/border-panel
    :north (create-members-header)
    :center (create-members-table)

    :vgap 5
    :border 11))

(defn create-members []
  (let [members-panel
         (seesaw-core/card-panel
           :id members-card-panel-id
           :items [[(create-members-table-panel) main-panel-key]
                   [(friend-selector/create (term/add)) add-member-panel-key]])]
    (seesaw-core/show-card! members-panel main-panel-key)
    members-panel))

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

(defn find-group-member-table
  "Finds the group member table in the given group panel."
  [panel]
  (view-utils/find-component panel group-member-table-id))

(defn destroy
  "Should be called right before the panel is destroyed."
  [panel]
  (korma-combobox-model/destroy-model (find-group-combobox panel))
  (korma-table-model/destroy-model (find-group-member-table panel)))

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

(defn find-group-display-label
  "Finds the group display label field."
  [view]
  (view-utils/find-component view group-display-id))

(defn selected-group
  "Returns the selected group in the group combobox."
  [view]
  (seesaw-core/selection (find-group-combobox view)))

(defn copy-selected-group-to-display
  "Copies the selected group to the display label."
  [view]
  (seesaw-core/config!
    (find-group-display-label view)
    :text (grouping-model/display (selected-group view))))

(defn show-group-editor-panel
  "Shows the group editor panel."
  [view]
  (seesaw-core/show-card!
    (find-group-manager-panel view) group-editor-card-id)
  view)

(defn show-group-filter-panel
  "Shows the group filter panel."
  [view]
  (seesaw-core/show-card! (find-group-manager-panel view) group-filter-card-id)
  view)

(defn show-group-display-panel
  "Shows the group display panel."
  [view]
  (seesaw-core/show-card! (find-group-manager-panel view) group-display-card-id)
  (copy-selected-group-to-display view)
  view)

(defn find-add-member-button
  "Finds the add member button in the given view."
  [view]
  (view-utils/find-component view add-member-button-id))

(defn find-members-panel
  "Finds the group members panel."
  [view]
  (view-utils/find-component view members-card-panel-id))

(defn show-main-members-panel
  "Shows the group members panel"
  [view]
  (seesaw-core/show-card! (find-members-panel view) main-panel-key)
  view)

(defn show-add-member-panel
  "Shows the add member panel"
  [view]
  (let [members-panel (find-members-panel view)]
    (friend-selector/load-data members-panel)
    (seesaw-core/show-card! members-panel add-member-panel-key)
  view))

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

(defn create-remove-listener
  "Creates a listener for the remove button in the group member table."
  []
  (fn [event]
    (let [button (seesaw-core/to-widget event)
          id-value (button-table-cell-editor/value-from button)]
      (future
        (grouping-profile/delete-grouping-profile
          (grouping-profile/find-grouping-profile id-value))))))

(defn group-combobox-action-listener
  "Updates the group member table to show the members of the selected group."
  [event]
  (let [group-panel (find-group-panel (view-utils/top-level-ancestor event))
        group-combobox (find-group-combobox group-panel)
        selected-group (seesaw-core/selection group-combobox)
        group-member-table (find-group-member-table group-panel)]
    (korma-table-model/destroy-model group-member-table)
    (seesaw-core/config! group-member-table
      :model (group-member-table-model/create selected-group))
    (table-renderer/set-button-table-cell-renderer
      group-member-table 2 group-member-table-model/view-button-cell-renderer)
    (button-table/create-table-button
      group-member-table 3
      (view-utils/create-link-button :text (term/remove) :background :white)
      (create-remove-listener))))

(defn attach-group-combobox-action-listener
  "Attaches the group combobox action listener to the group combobox."
  [view]
  (let [group-combobox (find-group-combobox view)]
    (seesaw-core/listen group-combobox
      :action-performed group-combobox-action-listener)
    (seesaw-core/selection!
      group-combobox
      (select-keys
        (grouping-model/find-grouping
          (grouping-model/find-everyone-id))
        [model-base/clojure-id grouping-model/display-key]))))

(defn add-member-listener
  "Shows the add member panel."
  [event]
  (let [frame (view-utils/top-level-ancestor event)]
    (show-group-display-panel frame)
    (show-add-member-panel frame)))

(defn attach-add-member-listener
  "Attaches the add member listener to the add member button in the given
view."
  [view]
  (view-utils/add-action-listener-to-button
    (find-add-member-button view) add-member-listener
    add-member-button-listener-key))

(deftype MemberSelector [view]
  friend-selector/FriendSelector
  (cancel-selection [this]
    (show-main-members-panel view)
    (show-group-filter-panel view))
  
  (submit-selection [this friends]
    (when friends
      (doseq [friend friends]
        (let [to-profile (friend-request/find-to-profile friend)]
          (when-let [group (selected-group view)]
            (grouping-profile/save
              (grouping-profile/create-grouping-profile group to-profile))))))
    (friend-selector/cancel-selection this)))

(defn initialize
  "Called when the panel is created to initialize the view by attaching
listeners and loading initial data."
  [view show-panel-fn]
  (attach-create-group-listener view)
  (attach-cancel-edit-listener view)
  (attach-save-edit-listener view)
  (attach-delete-group-listener view)
  (attach-edit-group-listener view)
  (attach-group-combobox-action-listener view)
  (attach-add-member-listener view)
  (friend-selector/initialize
    (friend-selector/find-panel view) (MemberSelector. view)))