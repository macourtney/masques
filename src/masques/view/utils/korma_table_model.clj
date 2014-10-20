(ns masques.view.utils.korma-table-model
  (:require [clojure.tools.logging :as logging]
            [masques.model.base :as model-base]
            [masques.view.utils.listener-list :as listener-list]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing.event TableModelEvent]
           [javax.swing.table TableModel]))

(def id-key :id)
(def text-key :text)
(def class-key :class)
(def edtiable?-key :edtiable?)
(def value-at-key :value-at)
(def update-value-key :update-value)

(defprotocol TableColumnProtocol
  "A model for displaying data to a table from a korma db."
  (column-id [this column-index]
    "Returns and identifier for the given column index.")

  (column-name [this column-id]
    "Returns the name of the given column for display in the header of the 
table.")
  
  (column-class [this column-id]
    "Returns the class of the items in the given column")
  
  (column-count [this]
    "Returns the total number of columns.")
  
  (cell-editable? [this row-index column-id]
    "Returns true if the cell at the given row index and column is editable."))

(defprotocol TableDbModel
  "An interface for getting the rows for a table."
  (db-entity [this]
    "Returns the entity this table db model uses and listen to.")
  
  (row-count [this]
    "Returns the number of values for this table.")
  
  (value-at [this row-index column-id]
     "Returns the value at the given index and column id.")
  
  (update-value [this row-index column-id value]
    "Updates the value at the given row index and column. Only called if
cell-editable? is true for the given row index and column.")
  
  (index-of [this record-or-id]
    "Returns the index of the given record or id in the model. If a record is
passed in, the record must have an id. If the record or id is not in this model,
then nil is retured."))

(defprotocol TableDBListeners
  "Handles all of the listeners for a table."
  
  (add-listener [this listener]
    "Adds the given listener to this table db listeners")
  
  (remove-listener [this listener]
    "Removes the given listener from this table db listeners")
  
  (listener-list [this]
    "Returns the listener list in this table db listeners")

  (remove-table-data-listeners [this table-data-listeners]
    "Removes the table data listeners.")
  
  (destroy [this]
    "Cleans up anything that needs to be cleaned up when this model will be
destroyed.")
  
  (initialize-listeners [this table-model]
    "Sets the table-model and performs any initialization necessary."))

(defprotocol KormaTableModelProtocol
  (destroy-table-model [this]
    "Cleans up anything that needs to be cleaned up when the model is ready to
be destroyed.")
  
  (table-db-model [this]
    "Returns the table db model associated with this table model.")
  
  (table-db-listeners [this]
    "Returns the table db listeners associated with this table model."))

(deftype ColumnListDbModel [columns columns-map]

  TableColumnProtocol
  (column-id [this column-index]
    (id-key (nth columns column-index)))

  (column-name [this column-id]
    (text-key (get columns-map column-id)))
  
  (column-class [this column-id]
    (class-key (get columns-map column-id)))
  
  (column-count [this]
    (count columns))
  
  (cell-editable? [this row-index column-id]
    (or (edtiable?-key (get columns-map column-id)) false)))

;*******************************************************************************
; Table interceptor functions and types.
;*******************************************************************************

(defn table-changed
  "Notifies the given TableModelEvent of the table changed event."
  [listener table-model-event]
  (.tableChanged listener table-model-event))

(defn create-table-model-event
  "Creates a new TableModelEvent with the given source type start and end index."
  [source type start-index end-index]
  (TableModelEvent.
    source start-index end-index TableModelEvent/ALL_COLUMNS type))

(defn notify-all-of-interval-added
  "Notifies all of the listeners in the given ListDataListenerList of the
interval added event."
  [table-data-listeners table-model-event]
  (seesaw-core/invoke-later
    (listener-list/notify-all-listeners
      table-data-listeners #(table-changed % table-model-event))))

(defn notify-all-of-contents-changed
  "Notifies all of the listeners in the given ListDataListenerList of the
contents changed event."
  [table-data-listeners table-model-event]
  (seesaw-core/invoke-later
    (listener-list/notify-all-listeners
      table-data-listeners #(table-changed % table-model-event))))

(defn notify-all-of-interval-removed
  "Notifies all of the listeners in the given ListDataListenerList of the
interval removed event."
  [table-data-listeners table-model-event]
  (seesaw-core/invoke-later
    (listener-list/notify-all-listeners
      table-data-listeners #(table-changed % table-model-event))))

(defn notify-model-of-update
  "Notifies all of the listeners in the given KormaTableModelProtocol that the
record with the given id has been editted."
  [table-model id]
  (when table-model
    (when-let [record-index (index-of (table-db-model table-model) id)]
      (notify-all-of-contents-changed
        (listener-list (table-db-listeners table-model))
        (create-table-model-event table-model TableModelEvent/UPDATE
                                  record-index record-index)))))

(defn notify-model-of-insert
  "Notifies all of the listeners in the given KormaTableModelProtocol that the
record with the given id has been added."
  [table-model id]
  (when table-model
    (when-let [record-index (index-of (table-db-model table-model) id)]
      (notify-all-of-interval-added
        (listener-list (table-db-listeners table-model))
        (create-table-model-event table-model TableModelEvent/INSERT
                                  record-index record-index)))))

(defn notify-model-of-delete
  "Notifies all of the listeners in the given KormaTableModelProtocol that the
record with the given id has been added."
  [table-model record-index]
  (when (and table-model record-index)
    (notify-all-of-interval-removed
      (listener-list (table-db-listeners table-model))
      (create-table-model-event table-model TableModelEvent/DELETE
        record-index record-index))))

(defn create-model-delete-interceptor
  "Creates a model interceptor which notifies all of the table data listeners in
the given model that a record was deleted."
  [table-model]
  (fn [action record]
    (let [record-index (index-of (table-db-model table-model) record)
          output (action record)]
      (notify-model-of-delete table-model record-index)
      output)))

(defn create-model-insert-interceptor
  "Creates a model interceptor which notifies all of the table data listeners in
the given model that a record was inserted."
  [table-model]
  (fn [action record]
    (let [record-id (action record)]
      (notify-model-of-insert table-model record-id)
      record-id)))

(defn create-model-update-interceptor
  "Creates a model interceptor which notifies all of the table data listeners in
the given model that a record was updated."
  [table-model]
  (fn [action record]
    (let [record-id (action record)]
      (notify-model-of-update table-model record-id)
      record-id)))

(deftype TableInterceptors [entity table-model]
  model-base/InterceptorProtocol
  (interceptor-entity [this]
    entity)
  
  (create-insert-interceptor [this]
    (create-model-insert-interceptor table-model))
  
  (create-update-interceptor [this]
    (create-model-update-interceptor table-model))
  
  (create-delete-interceptor [this]
    (create-model-delete-interceptor table-model))
  
  (create-change-interceptor [this]
    nil))

(defn create-table-interceptors
  "Creates a new TableInterceptors with the given entity and table db model."
  [entity table-model]
  (TableInterceptors. entity table-model))

(deftype KormaTableDbListeners
  [db-model table-model table-data-listeners interceptor-manager]

  TableDBListeners
  (add-listener [this listener]
    (listener-list/add-listener table-data-listeners listener))
  
  (remove-listener [this listener]
    (listener-list/remove-listener table-data-listeners listener))
  
  (listener-list [this]
    table-data-listeners)
  
  (remove-table-data-listeners [this _]
    (when @interceptor-manager
      (model-base/remove-interceptors @interceptor-manager)
      (reset! interceptor-manager nil))
    (when table-data-listeners
      (doseq [listener (listener-list/listeners table-data-listeners)]
        (listener-list/remove-listener table-data-listeners listener))))

  (destroy [this]
    (remove-table-data-listeners this nil))
  
  (initialize-listeners [this new-table-model]
    (reset! table-model new-table-model)
    (when new-table-model
      (reset!
        interceptor-manager
        (model-base/create-interceptor-manager
          (create-table-interceptors (db-entity db-model) new-table-model))))))

(deftype KormaTableModel [column-model db-model listener-model]

  KormaTableModelProtocol
  (destroy-table-model [this]
    (destroy listener-model))
  
  (table-db-model [this]
    db-model)
  
  (table-db-listeners [this]
    listener-model)
  
  TableModel
  (getColumnName [this column-index]
    (column-name column-model (column-id column-model column-index)))

  (getColumnClass [this column-index]
    (or (column-class column-model (column-id column-model column-index))
        Object))
  
  (getColumnCount [this]
    (column-count column-model))

  (getRowCount [this]
    (row-count db-model))
  
  (getValueAt [this row-index column-index]
    (value-at db-model row-index (column-id column-model column-index)))
  
  (setValueAt [this value row-index column-index]
    (update-value
      db-model row-index (column-id column-model column-index) value))
  
  (isCellEditable [this row-index column-index]
    (cell-editable?
      column-model row-index (column-id column-model column-index)))
  
  (removeTableModelListener [this listener]
    (remove-listener listener-model listener))
  
  (addTableModelListener [this listener]
    (add-listener listener-model listener)))

(defn create
  "Creates a new korma table model with the given db model."
  ([column-model db-model]
    (create column-model db-model
            (KormaTableDbListeners.
              db-model (atom nil) (listener-list/create) (atom nil))))
  ([column-model db-model table-db-listeners]
    (let [table-model (KormaTableModel. column-model db-model table-db-listeners)]
      (initialize-listeners table-db-listeners table-model)
      table-model)))

(defn create-column-map
  "Creates a map from the column id to the column description map from the list
of column descriptions."
  [columns]
  (reduce #(assoc %1 (id-key %2) %2) {} columns))

(defn create-from-columns
  "Creates a new korma table model with the given columns and column value
list."
  [columns table-db-model]
  (create (ColumnListDbModel. columns (create-column-map columns))
          table-db-model))

(defn find-value-at-fn
  "Returns the value at function from the column map for the column with the
given id. If the column does not have a value at fn, then this function returns
a function which takes any number of arguments but only returns an empty
string."
  [columns-map column-id]
  (or (value-at-key (get columns-map column-id))
      (fn [& args] "")))

(defn find-update-value-fn
  "Returns the update value function from the column map for the column with the
given id. If the column does not have a update value fn, then this function returns
a function which takes any number of arguments but does nothing."
  [columns-map column-id]
  (or (update-value-key (get columns-map column-id))
      (fn [& args])))

(defn destroy-model
  "Retrieves the KormaTableModel from the given table and runs the destroy
function on it."
  [table]
  (when-let [table-model (seesaw-core/config table :model)]
    (when (instance? KormaTableModel table-model)
      (destroy-table-model table-model))))