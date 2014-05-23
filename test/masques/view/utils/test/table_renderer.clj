(ns masques.view.utils.test.table-renderer
  (:use clojure.test
        masques.view.utils.table-renderer)
  (:require [seesaw.core :as seesaw-core])
  (:import [javax.swing ImageIcon JLabel]))

(deftest test-create
  (let [test-value "test value"
        sent-value (atom nil)
        test-renderer-fn (fn [table value isSelected hasFocus row column]
                           (reset! sent-value value)
                           (JLabel. value))
        test-renderer (create test-renderer-fn)]
    (is (nil? @sent-value))
    (is (instance? JLabel
          (.getTableCellRendererComponent
            test-renderer nil test-value false false 0 0)))
    (is (= @sent-value test-value))))

(deftest test-set-renderer
  (let [test-value "test value"
        sent-value (atom nil)
        test-renderer-fn (fn [table value isSelected hasFocus row column]
                           (reset! sent-value value)
                           (JLabel. value))
        test-table (seesaw-core/table :id :test-table
                                      :model [:columns [:foo] :rows [["bar"]]])]
    (set-renderer test-table 0 test-renderer-fn)))

(deftest test-image-cell-renderer
  (let [test-icon (ImageIcon. (ClassLoader/getSystemResource "profile.png"))
        component (image-cell-renderer nil test-icon false false 0 0)]
    (is (instance? JLabel component))
    (is (= (.getIcon component) test-icon))))

(deftest test-set-column-width
  (let [column-index 0
        width 20
        test-table (seesaw-core/table :id :test-table
                                      :model [:columns [:foo] :rows [["bar"]]])]
    (set-column-width test-table column-index width)
    (is (= width
          (.getMaxWidth
            (.getColumn (.getColumnModel test-table) column-index))))))