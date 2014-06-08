(ns masques.view.utils.korma-table-model
  (:import [javax.swing.table TableModel]))

(def id-key :id)
(def text-key :text)
(def class-key :class)
(def edtiable?-key :edtiable?)
(def value-at-key :value-at)
(def update-value-key :update-value)

(defprotocol DbModel
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

(defprotocol ColumnValueList
  "An interface for getting the rows for a table."
  (row-count [this]
    "Returns the number of values for this list.")
  
  (value-at [this row-index column-id]
     "Returns the value at the given index and column id.")
  
  (update-value [this row-index column-id value]
    "Updates the value at the given row index and column. Only called if
cell-editable? is true for the given row index and column."))

(deftype ColumnListDbModel
  [columns columns-map column-value-list]

  DbModel
  (column-id [this column-index]
    (id-key (nth columns column-index)))

  (column-name [this column-id]
    (text-key (get columns-map column-id)))
  
  (column-class [this column-id]
    (class-key (get columns-map column-id)))
  
  (column-count [this]
    (count columns))
  
  (cell-editable? [this row-index column-id]
    (or (edtiable?-key (get columns-map column-id)) false))
  
  ColumnValueList
  (row-count [this]
    (row-count column-value-list))
  
  (value-at [this row-index column-id]
    (value-at column-value-list row-index column-id))

  (update-value [this row-index column-id value]
    (update-value column-value-list row-index column-id value)))

(deftype KormaTableModel [table-model-listener-set db-model]
  TableModel
  (addTableModelListener [this listener]
    (reset! table-model-listener-set (conj @table-model-listener-set listener)))
  
  (getColumnName [this column-index]
    (column-name db-model (column-id db-model column-index)))

  (getColumnClass [this column-index]
    (or (column-class db-model (column-id db-model column-index)) Object))
  
  (getColumnCount [this]
    (column-count db-model))

  (getRowCount [this]
    (row-count db-model))
  
  (getValueAt [this row-index column-index]
    (value-at db-model row-index (column-id db-model column-index)))
  
  (isCellEditable [this row-index column-index]
    (cell-editable? db-model row-index (column-id db-model column-index)))
  
  (removeTableModelListener [this listener]
    (reset! table-model-listener-set (disj @table-model-listener-set listener)))
  
  (setValueAt [this value row-index column-index]
    (update-value db-model row-index (column-id db-model column-index) value)))

(defn create
  "Creates a new korma table model with the given db model."
  [db-model]
  (KormaTableModel. (atom #{}) db-model))

(defn create-column-map
  "Creates a map from the column id to the column description map from the list
of column descriptions."
  [columns]
  (reduce #(assoc %1 (id-key %2) %2) {} columns))

(defn create-from-columns
  "Creates a new korma table model with the given columns and column value
list."
  [columns column-value-list]
  (create (ColumnListDbModel.
            columns (create-column-map columns) column-value-list)))

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