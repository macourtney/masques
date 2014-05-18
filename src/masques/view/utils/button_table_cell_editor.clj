(ns masques.view.utils.button-table-cell-editor
  (:require [clojure.tools.logging :as logging]
            [masques.view.utils :as utils]
            [masques.view.utils.listener-list :as listener-list])
  (:import [javax.swing.event ChangeEvent]
           [javax.swing.table TableCellEditor]))

(def value-key :value)

(defn value-from
  "Returns the value attached to the given button by the ButtonTableCellEditor."
  [button]
  (utils/retrieve-component-property button value-key))

(defn create-cancel-notifier
  "Creates a editing canceled notifier for the cell editor listeners. The given
event should be the change event you want to pass to all of the listeners'
editingCanceled()."
  [event]
  (fn [listener]
    (.editingCanceled listener event)))

(defn create-stopped-notifier
  "Creates a editing stopped notifier for the cell editor listeners. The given
event should be the change event you want to pass to all of the listeners'
editingCanceled()."
  [event]
  (fn [listener]
    (.editingStopped listener event)))

(deftype ButtonTableCellEditor [cell-editor-listeners button]
  TableCellEditor
  (getTableCellEditorComponent [this table value isSelected row column]
    (utils/save-component-property button value-key value)
    button)
  
  (getCellEditorValue [this]
    button)
  
  (cancelCellEditing [this]
    (listener-list/notify-all-listeners
      cell-editor-listeners (create-cancel-notifier (ChangeEvent. button))))
  
  (stopCellEditing [this]
    (listener-list/notify-all-listeners
      cell-editor-listeners (create-stopped-notifier (ChangeEvent. button)))
    true)
  
  (isCellEditable [this event]
    true)
  
  (shouldSelectCell [this event]
    false)
  
  (addCellEditorListener [this listener]
    (listener-list/add-listener cell-editor-listeners listener))
  
  (removeCellEditorListener [this listener]
    (listener-list/remove-listener cell-editor-listeners listener)))

(defn create
  "Creates a new button table cell editor. The button will have the given text
and when pressed will run the given listener."
  [text listener]
  (let [button (utils/create-link-button
                 :text text
                 :listen [:action-performed listener]
                 :background :white)]
    (ButtonTableCellEditor. (listener-list/create) button)))

(defn set-cell-editor
  "Sets the table cell editor for the given table to a button table cell editor
with the given text and listener."
  [table column-index text listener]
  (.setCellEditor (.getColumn (.getColumnModel table) column-index)
    (create text listener)))