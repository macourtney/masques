(ns masques.view.group.all-groups-combobox-model
  (:require [masques.model.grouping :as grouping-model]
            [masques.view.utils.korma-combobox-model :as korma-combobox-model]
            [masques.view.utils :as utils]))

(defn create-grouping-model-delete-interceptor
  "Creates a grouping model interceptor which notifies all of the given list data
interceptor that a group was deleted."
  [model]
  (fn [action record]
    (let [record-index (korma-combobox-model/index-of model record)
          output (action record)]
      (korma-combobox-model/notify-model-of-delete model record-index)
      output)))

(defn create-grouping-model-insert-interceptor
  "Creates a grouping model interceptor which notifies all of the given list
data interceptors that a group was inserted."
  [model]
  (fn [action record]
    (let [record-id (action record)]
      (korma-combobox-model/notify-model-of-insert model record-id)
      record-id)))

(defn create-grouping-model-update-interceptor
  "Creates a grouping model interceptor which notifies all of the given list
data interceptors that a group was updated."
  [model]
  (fn [action record]
    (let [record-id (action record)]
      (korma-combobox-model/notify-model-of-update model record-id)
      record-id)))

(deftype AllGroupsComboboxModel
  [list-data-listeners grouping-model-delete-interceptor
   grouping-model-insert-interceptor grouping-model-update-interceptor]

  korma-combobox-model/DbComboBoxModel
  (record-count [this]
    (grouping-model/count-groups))
  
  (record-at [this index]
    (grouping-model/find-combobox-group index))
  
  (index-of [this record-or-id]
    (grouping-model/combobox-index-of record-or-id))
  
  (set-list-data-listeners [this new-list-data-listeners]
    (when @list-data-listeners
      (korma-combobox-model/remove-list-data-listeners
        this @list-data-listeners))
    (reset! list-data-listeners new-list-data-listeners)
    (reset! grouping-model-delete-interceptor
            (create-grouping-model-delete-interceptor this))
    (reset! grouping-model-insert-interceptor
            (create-grouping-model-insert-interceptor this))
    (reset! grouping-model-update-interceptor
            (create-grouping-model-update-interceptor this))
    (grouping-model/add-grouping-delete-interceptor
      @grouping-model-delete-interceptor)
    (grouping-model/add-grouping-insert-interceptor
      @grouping-model-insert-interceptor)
    (grouping-model/add-grouping-update-interceptor
      @grouping-model-update-interceptor))
  
  (remove-list-data-listeners [this _]
    (grouping-model/remove-grouping-delete-interceptor
      @grouping-model-delete-interceptor)
    (grouping-model/remove-grouping-insert-interceptor
      @grouping-model-insert-interceptor)
    (grouping-model/remove-grouping-update-interceptor
      @grouping-model-update-interceptor)
    (reset! grouping-model-delete-interceptor nil)
    (reset! grouping-model-insert-interceptor nil)
    (reset! grouping-model-update-interceptor nil)
    (reset! list-data-listeners nil))
  
  (list-data-listeners [this]
    @list-data-listeners)
  
  (destroy [this]
    (korma-combobox-model/remove-list-data-listeners this nil)))

(defn create
  "Creates a new combobox model for use in the groups combobox."
  []
  (korma-combobox-model/create
    (AllGroupsComboboxModel. (atom nil) (atom nil) (atom nil) (atom nil))))