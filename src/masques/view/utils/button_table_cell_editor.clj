(ns masques.view.utils.button-table-cell-editor
  (:require [clojure.tools.logging :as logging]
            [masques.view.utils :as utils])
  (:import [javax.swing.event ChangeEvent]
           [javax.swing.table TableCellEditor]))

(def value-key :value)

(defn value-from
  "Returns the value attached to the given button by the ButtonTableCellEditor."
  [button]
  (utils/retrieve-component-property button value-key))

(deftype ButtonTableCellEditor [cell-editor-listeners button]
  TableCellEditor
  (getTableCellEditorComponent [this table value isSelected row column]
    (utils/save-component-property button value-key value)
    button)
  
  (getCellEditorValue [this]
    button)
  
  (cancelCellEditing [this]
    (doseq [cell-editor-listener @cell-editor-listeners]
      (.editingCanceled cell-editor-listener (ChangeEvent. button))))
  
  (stopCellEditing [this]
    (doseq [cell-editor-listener @cell-editor-listeners]
      (.editingStopped cell-editor-listener (ChangeEvent. button)))
    true)
  
  (isCellEditable [this event]
    true)
  
  (shouldSelectCell [this event]
    false)
  
  (addCellEditorListener [this listener]
    (reset! cell-editor-listeners (conj @cell-editor-listeners listener)))
  
  (removeCellEditorListener [this listener]
    (reset! cell-editor-listeners (disj @cell-editor-listeners listener))))

(defn create
  "Creates a new button table cell editor. The button will have the given text
and when pressed will run the given listener."
  [text listener]
  (let [button (utils/create-link-button
                 :text text
                 :listen [:action-performed listener]
                 :background :white)]
    (ButtonTableCellEditor. (atom #{}) button)))

(defn set-cell-editor
  "Sets the table cell editor for the given table to a button table cell editor
with the given text and listener."
  [table column-index text listener]
  (.setCellEditor (.getColumn (.getColumnModel table) column-index)
    (create text listener)))