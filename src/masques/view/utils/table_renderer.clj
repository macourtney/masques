(ns masques.view.utils.table-renderer
  (:import [javax.swing.table TableCellRenderer]
           [javax.swing JLabel]))

(deftype TableRenderer [table-renderer-fn]
  TableCellRenderer
  (getTableCellRendererComponent
    [this table value isSelected hasFocus row column]
    (table-renderer-fn table value isSelected hasFocus row column)))

(defn create
  "Creates a new TableCellRenderer which simply forwards requests to the given
table-renderer-fn. Table-renderer-fn must take the parameters:
[table value isSelected hasFocus row column]"
  [table-renderer-fn]
  (TableRenderer. table-renderer-fn))

(defn set-renderer
  "Sets the table cell renderer for the given table to the given
table-renderer-fn."
  [table column-index table-renderer-fn]
  (.setCellRenderer (.getColumn (.getColumnModel table) column-index)
    (create table-renderer-fn)))

(defn image-cell-renderer
  "Creates a JLabel to display the image given as an ImagaIcon in value."
  [table value isSelected hasFocus row column]
  (JLabel. value))