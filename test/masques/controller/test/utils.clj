(ns masques.controller.test.utils
  (:use clojure.test
        masques.controller.utils)
  (:require [masques.view.utils :as view-utils])
  (:import [java.awt.event ItemListener]
           [javax.swing JPanel JComboBox]))

(defn test-item-listener [e]
  "blah")

(deftest test-create-item-listener
  (let [item-listener (create-item-listener test-item-listener)]
    (is item-listener)
    (is (instance? ItemListener item-listener))))

(deftest test-attach-item-listener
  (let [test-combobox (new JComboBox)
        original-listener-count (count (.getItemListeners test-combobox))]
    (attach-item-listener test-combobox test-item-listener)
    (is (= (count (.getItemListeners test-combobox)) (inc original-listener-count)))))

(deftest test-enable-disable-widgets
  (let [test-combobox (new JComboBox)
        test-panel (doto (new JPanel) (.add test-combobox))]
    (is (enableable-widget? test-panel))
    (is (= (count (enableable-widgets test-panel)) 3))
    (enable-subwidgets test-panel false)
    (is (not (.isEnabled test-panel)))
    (is (not (.isEnabled test-combobox)))
    (enable-widget test-panel)
    (is (.isEnabled test-panel))
    (is (.isEnabled test-combobox))
    (disable-widget test-panel)
    (is (not (.isEnabled test-panel)))
    (is (not (.isEnabled test-combobox)))))

(deftest test-attach-detach-listeners
  (let [test-combobox (new JComboBox)
        original-listener-count (count (.getItemListeners test-combobox))
        item-listener (create-item-listener test-item-listener)
        add-item-listener #(.addItemListener test-combobox %)
        remove-item-listener #(.removeItemListener test-combobox %)
        item-listener-key "test-item-listener"]
    (is (nil? (view-utils/retrieve-component-property test-combobox item-listener-key))) 
    (attach-and-save-listener test-combobox add-item-listener item-listener-key item-listener)
    (is (= (count (.getItemListeners test-combobox)) (inc original-listener-count)))
    (is (= (view-utils/retrieve-component-property test-combobox item-listener-key) item-listener))
    (detach-and-remove-listener test-combobox remove-item-listener item-listener-key)
    (is (= (count (.getItemListeners test-combobox)) original-listener-count))))