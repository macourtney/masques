(ns masques.view.subviews.korma-table-model
  (:import [javax.swing.table TableModel]))

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
  
  (row-count [this] "Returns the number of rows for this model.")
  
  (value-at [this row-index column-id]
    "Returns the value at the given row index and column")
  
  (cell-editable? [this row-index column-id]
    "Returns true if the cell at the given row index and column is editable.")
  
  (update-value [this row-index column-id value]
    "Updates the value at the given row index and column. Only called if
cell-editable? is true for the given row index and column."))

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

(defn create [db-model]
  (KormaTableModel. (atom #{}) db-model))