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

(defn set-column-width
  "Sets the width of the column at the given index to the given width."
  [table column-index width]
  (let [column (.getColumn (.getColumnModel table) column-index)]
    (.setMaxWidth column width)
    (.setMinWidth column width)
    (.setWidth column width)))

(defn set-button-table-cell-renderer
  "Sets the table cell renderer for the given column index on the given table.
You can also set the column width. If no width is given, then it is set to 80."
  ([table column-index renderer]
    (set-button-table-cell-renderer table column-index renderer 80))
  ([table column-index renderer width]
    (set-renderer table column-index renderer)
    (set-column-width table column-index width)))