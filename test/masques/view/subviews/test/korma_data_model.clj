(ns masques.view.subviews.test.korma-data-model
  (:use clojure.test
        masques.view.subviews.korma-table-model))

(def column0 "test-column-0")
(def column1 "test-column-1")

(def test-columns [column0 column1])

(def row0 { :test-column-0 1 :test-column-1 "foo" })
(def row1 { :test-column-0 2 :test-column-1 "bar" })

(def test-rows [row0 row1])

(deftype TestTableModel []
  DbModel
  (column-id [this column-index]
    (nth test-columns column-index))

  (column-name [this column-id]
    column-id)
  
  (column-class [this column]
    (condp = column
      column0 Integer
      column1 String
      nil))
  
  (column-count [this]
    (count test-columns))
  
  (row-count [this]
    (count test-rows))
  
  (value-at [this row-index column]
    ((keyword column) (nth test-rows row-index)))
  
  (cell-editable? [this row-index column]
    false)
  
  (update-value [this _ _ _]))

(deftest test-create
  (let [korma-table-model (create (new TestTableModel))]
    (is (= (.getColumnName korma-table-model 0) column0))
    (is (= (.getColumnClass korma-table-model 1) String))
    (is (= (.getColumnCount korma-table-model) (count test-columns)))
    (is (= (.getRowCount korma-table-model) (count test-rows)))
    (is (= (.getValueAt korma-table-model 0 0) (:test-column-0 row0)))
    (is (= (.getValueAt korma-table-model 1 1) (:test-column-1 row1)))
    (is (not (.isCellEditable korma-table-model 0 0)))
    (is (= (.getValueAt korma-table-model 0 0) (:test-column-0 row0)))))