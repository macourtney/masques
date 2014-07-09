(ns masques.view.utils.test.korma-table-model
  (:require test.init
            [clojure.tools.logging :as logging]
            [masques.model.base :as model-base])
  (:use clojure.test
        masques.view.utils.korma-table-model))

(def column0 "test-column-0")
(def column1 "test-column-1")

(def test-columns [column0 column1])

(def row0 { :test-column-0 1 :test-column-1 "foo" })
(def row1 { :test-column-0 2 :test-column-1 "bar" })

(def test-rows [row0 row1])

(deftype TestColumnModel []
  TableColumnProtocol
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
  
  (cell-editable? [this row-index column]
    false))

(deftype TestDbModel []

  TableDbModel
  (db-entity [this]
    nil)
  
  (row-count [this] (count test-rows))
  
  (value-at [this row-index column-id]
    ((keyword column-id) (nth test-rows row-index)))
  
  (update-value [this _ _ _]
    )
  
  (index-of [this record-or-id]
    (some
      #(when (= (model-base/id record-or-id) (model-base/id (first %1)))
         (second %1))
      (map list test-rows (range)))))

(deftype TestDBListeners [table-data-listeners table-model]

  TableDBListeners
  (remove-table-data-listeners [this _]
    (reset! table-data-listeners nil))
  
  (table-data-listeners [this]
    @table-data-listeners)
  
  (destroy [this]
    (remove-table-data-listeners this nil))
  
  (initialize-listeners [this new-table-model new-table-data-listeners]
    (reset! table-model new-table-model)
    (when @table-data-listeners
      (remove-table-data-listeners this @table-data-listeners))
    (reset! table-data-listeners new-table-data-listeners)))

(deftest test-create
  (let [table-model-atom (atom nil)
        table-data-listeners-atom (atom nil)
        test-db-listeners (TestDBListeners.
                            table-data-listeners-atom table-model-atom)
        korma-table-model (create (new TestColumnModel) (new TestDbModel)
                                  test-db-listeners)]
    (is (= korma-table-model @table-model-atom))
    (is @table-data-listeners-atom)
    (is (= (.getColumnName korma-table-model 0) column0))
    (is (= (.getColumnClass korma-table-model 1) String))
    (is (= (.getColumnCount korma-table-model) (count test-columns)))
    (is (= (.getRowCount korma-table-model) (count test-rows)))
    (is (= (.getValueAt korma-table-model 0 0) (:test-column-0 row0)))
    (is (= (.getValueAt korma-table-model 1 1) (:test-column-1 row1)))
    (is (not (.isCellEditable korma-table-model 0 0)))
    (is (= (.getValueAt korma-table-model 0 0) (:test-column-0 row0)))))