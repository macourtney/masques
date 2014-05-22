(ns masques.view.utils.test.list-renderer
  (:use clojure.test
        masques.view.utils.list-renderer)
  (:require [seesaw.core :as seesaw-core])
  (:import [javax.swing ImageIcon JLabel]))

(deftest test-create
  (let [test-value "test value"
        sent-value (atom nil)
        test-renderer-fn (fn [list value index isSelected cellHasFocus]
                           (reset! sent-value value)
                           (JLabel. value))
        test-renderer (create test-renderer-fn)]
    (is (nil? @sent-value))
    (.getListCellRendererComponent test-renderer nil test-value 0 false false)
    (is (= @sent-value test-value))))

(deftest test-set-renderer
  (let [test-value "test value"
        sent-value (atom nil)
        test-renderer-fn (fn [list value index isSelected cellHasFocus]
                           (reset! sent-value value)
                           (JLabel. value))
        test-list (seesaw-core/listbox :id :test-list)]
    (set-renderer test-list test-renderer-fn)))

(deftest test-image-cell-renderer
  (let [test-icon (ImageIcon. (ClassLoader/getSystemResource "profile.png"))
        component (image-cell-renderer nil test-icon 0 false false)]
    (is (instance? JLabel component))
    (is (= (.getIcon component) test-icon))))

(deftest test-create-record-text-cell-renderer
  (let [test-key :value
        test-value "bar"
        test-record { test-key test-value }
        test-renderer (create-record-text-cell-renderer test-key)
        component (test-renderer nil test-record 0 false false)]
    (is (instance? JLabel component))
    (is (= (.getText component) test-value))))