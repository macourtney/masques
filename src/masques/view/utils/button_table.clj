(ns masques.view.utils.button-table
  (:require [clojure.tools.logging :as logging]
            [masques.view.utils.button-table-cell-editor :as button-table-cell-editor]
            [masques.view.utils.table-renderer :as table-renderer])
  (:use [masques.view.utils.korma-table-model :exclude [create]]))

(defn create-table-button-column-map
  "Creates a table column map for a table button. The column has no header and
has an integer value which should be the id of the record at a given row."
  [column-id id-value-at]
  { id-key column-id
    text-key ""
    class-key Integer
    edtiable?-key true
    value-at-key id-value-at })

(defn create-button-renderer
  "Creates a table renderer which simply returns the given button as the
rendered cell."
  [button]
  (fn [_ _ _ _ _ _]
    button))

(defn create-table-button
  "Adds the given button to the given table as the renderer and editor for the
given column. The given listener is used as the action for the button."
  [table column-number button listener]
  (table-renderer/set-button-table-cell-renderer
    table column-number (create-button-renderer button))
  (button-table-cell-editor/set-cell-editor 
      table column-number
      (button-table-cell-editor/create-from-button button listener)))