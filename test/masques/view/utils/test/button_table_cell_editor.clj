(ns masques.view.utils.test.button-table-cell-editor
  (:use clojure.test
        masques.view.utils.button-table-cell-editor)
  (:import [javax.swing.event CellEditorListener]))

(def called? (atom false))
(def canceled? (atom false))
(def stopped? (atom false))

(deftype TestCellEditorListener []
  CellEditorListener
  
  (editingCanceled [this change-event]
    (reset! canceled? true))
  
  (editingStopped [this change-event]
    (reset! stopped? true)))

(defn test-listener [event]
  (reset! called? true))

(deftest test-create
  (let [button-table-cell-editor (create "test" test-listener)]
    (is (.getCellEditorValue button-table-cell-editor))
    (is (.isCellEditable button-table-cell-editor nil))
    (is (not (.shouldSelectCell button-table-cell-editor nil)))
    (is (.getTableCellEditorComponent
          button-table-cell-editor nil :test-value false 0 0))
    (.addCellEditorListener
      button-table-cell-editor (new TestCellEditorListener))
    (is (not @canceled?))
    (is (not @stopped?))
    (.cancelCellEditing button-table-cell-editor)
    (is @canceled?)
    (reset! canceled? false)
    (is (not @stopped?))
    (.stopCellEditing button-table-cell-editor)
    (is @stopped?)
    (is (not @canceled?))))