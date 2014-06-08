(ns masques.view.group.group-member-table-model
  (:require [clj-internationalization.term :as term]
            [masques.model.base :as model-base]
            [masques.model.grouping-profile :as grouping-profile-model]
            [masques.model.profile :as profile-model])
  (:use [masques.view.utils.korma-table-model :exclude [create]]))

(def date-added-column-id :date-added)
(def name-column-id :name)
(def remove-column-id :remove)
(def view-column-id :view)

(defn id-value-at
  "A value at function which returns the id of the grouping-profile for the
given group at the given row index."
  [group-id row-index column-id]
  (model-base/id
    (grouping-profile-model/find-table-grouping-profile group-id row-index)))

(def columns [{ id-key name-column-id
                text-key (term/name)
                class-key String
                value-at-key
                  (fn [group-id row-index column-id]
                    (profile-model/alias
                      (grouping-profile-model/find-profile
                        (grouping-profile-model/find-table-grouping-profile
                          group-id row-index))))}
              { id-key date-added-column-id
                text-key (term/date-added)
                class-key String
                value-at-key
                  (fn [group-id row-index column-id]
                    (str
                      (model-base/find-created-at
                        (grouping-profile-model/find-table-grouping-profile
                          group-id row-index)))) }
              { id-key view-column-id
                text-key (term/view)
                class-key Integer
                edtiable?-key true
                value-at-key id-value-at }
              { id-key remove-column-id
                text-key (term/remove)
                class-key Integer
                edtiable?-key true
                value-at-key id-value-at }])

(deftype GroupMemberTableModel [group-id column-map]

  ColumnValueList
  (row-count [this]
    (grouping-profile-model/count-grouping-profiles group-id))
  
  (value-at [this row-index column-id]
    ((find-value-at-fn column-map column-id)
      group-id row-index column-id))
  
  (update-value [this row-index column-id value]
    ((find-update-value-fn column-map column-id)
      group-id row-index column-id value)))

(defn create
  "Creates a new group member table model which shows all of the group members
of the group with the given id."
  [group-id]
  (create-from-columns
    columns
    (GroupMemberTableModel.
      (model-base/id group-id) (create-column-map columns))))