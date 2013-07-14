(ns masques.controller.test.utils
  (:use clojure.test
        masques.controller.utils)
  (:import [java.awt.event ItemListener]
           [javax.swing JPanel JComboBox]))

(defn test-item-listener [e]
  "blah")

(deftest test-create-item-listener
  (let [item-listener (create-item-listener test-item-listener)]
    (is item-listener)
    (is (instance? ItemListener item-listener))))

(deftest test-attach-item-listener
  (let [test-combobox (new JComboBox)]
    (is (= (count (.getItemListeners test-combobox)) 2))
    (attach-item-listener test-combobox test-item-listener)
    (is (= (count (.getItemListeners test-combobox)) 3))))

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

(deftest test-component-property
  (let [test-key :key
        test-value :value
        test-panel (new JPanel)]
    (save-component-property test-panel test-key test-value)
    (is (= (retrieve-component-property test-panel test-key) test-value))
    (is (= (remove-component-property test-panel test-key) test-value))
    (is (nil? (retrieve-component-property test-panel test-key)))))