(ns masques.view.group.all-groups-combobox-model
  (:require [masques.model.grouping :as grouping-model]
            [masques.view.utils.korma-combobox-model :as korma-combobox-model]
            [masques.view.utils :as utils]))

(defn create-grouping-model-listener
  "Creates a grouping model listener which notifies all of the given list data
listeners that a group changed."
  [model list-data-listeners]
  (fn [id]
    (korma-combobox-model/notify-all-of-contents-changed
      list-data-listeners
      (korma-combobox-model/create-change-all-list-data-event
        model (grouping-model/count-groups)))))

(deftype AllGroupsComboboxModel [list-data-listeners grouping-model-listener]
  korma-combobox-model/DbComboBoxModel
  (record-count [this]
    (grouping-model/count-groups))
  
  (record-at [this index]
    (grouping-model/find-combobox-group index))
  
  (set-list-data-listeners [this new-list-data-listeners]
    (when @list-data-listeners
      (korma-combobox-model/remove-list-data-listeners
        this @list-data-listeners))
    (reset! list-data-listeners new-list-data-listeners)
    (reset! grouping-model-listener
            (create-grouping-model-listener this @list-data-listeners))
    (grouping-model/add-grouping-change-listener @grouping-model-listener))
  
  (remove-list-data-listeners [this _]
    (grouping-model/remove-grouping-change-listener @grouping-model-listener)
    (reset! grouping-model-listener nil)
    (reset! list-data-listeners nil))
  
  (destroy [this]
    (korma-combobox-model/remove-list-data-listeners this nil)))

(defn create
  "Creates a new combobox model for use in the groups combobox."
  []
  (korma-combobox-model/create (AllGroupsComboboxModel. (atom nil) (atom nil))))