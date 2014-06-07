(ns masques.view.group.group-member-table-model
  (:require [clj-internationalization.term :as term]
            [masques.model.base :as model-base]
            [masques.model.grouping :as grouping-model]
            [masques.view.utils.korma-table-model :as korma-table-model]
            [masques.view.utils :as utils]))

(def date-added-column-id :date-added)
(def name-column-id :name)
(def remove-column-id :remove)
(def view-column-id :view)

(def columns [{ korma-table-model/id-key name-column-id
                korma-table-model/text-key (term/name)
                korma-table-model/class-key String }
              { korma-table-model/id-key date-added-column-id
                korma-table-model/text-key (term/date-added)
                korma-table-model/class-key String }
              { korma-table-model/id-key view-column-id
                korma-table-model/text-key (term/view)
                korma-table-model/class-key Integer
                korma-table-model/edtiable?-key true }
              { korma-table-model/id-key remove-column-id
                korma-table-model/text-key (term/remove)
                korma-table-model/class-key Integer
                korma-table-model/edtiable?-key true }])

(deftype GroupMemberTableModel [group-id]

  korma-table-model/ColumnValueList
  (row-count [this] 0)
  
  (value-at [this row-index column-id]
    (condp = column-id
      name-column-id ""
      date-added-column-id ""
      view-column-id 0
      remove-column-id 0
      nil))
  
  (update-value [this _ _ _]))

(defn create
  "Creates a new group member table model which shows all of the group members
of the group with the given id."
  [group-id]
  (korma-table-model/create-from-columns
    columns
    (GroupMemberTableModel. (model-base/id group-id))))