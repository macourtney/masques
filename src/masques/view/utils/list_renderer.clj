(ns masques.view.utils.list-renderer
  (:import [javax.swing JLabel ListCellRenderer]))

(deftype ListRenderer [list-renderer-fn]
  ListCellRenderer
  (getListCellRendererComponent
    [this list value index isSelected cellHasFocus]
    (list-renderer-fn list value index isSelected cellHasFocus)))

(defn create
  "Creates a new ListCellRenderer which simply forwards requests to the given
list-renderer-fn. List-renderer-fn must take the parameters:
[list value index isSelected cellHasFocus]"
  [list-renderer-fn]
  (ListRenderer. list-renderer-fn))

(defn set-renderer
  "Sets the list cell renderer for the given list to the given
list-renderer-fn."
  [list list-renderer-fn]
  (.setRenderer list (create list-renderer-fn)))

(defn image-cell-renderer
  "Creates a JLabel to display the image given as an ImagaIcon in value."
  [list value index isSelected cellHasFocus]
  (JLabel. value))

(defn create-record-text-cell-renderer
  "Creates a cell renderer which displays the value of the display-key of the
value as text in a label."
  [display-key]
  (fn [list value index isSelected cellHasFocus]
    (JLabel. (str (get value display-key)))))