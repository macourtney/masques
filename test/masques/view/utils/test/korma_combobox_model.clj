(ns masques.view.utils.test.korma-combobox-model
  (:use clojure.test
        masques.view.utils.korma-combobox-model)
  (:require [masques.view.utils.listener-list :as listener-list]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing.event ListDataEvent ListDataListener]))

(def foo-record { :text "foo" })
(def bar-record { :text "bar" })
(def records [foo-record bar-record])

(deftype TestDbComboBoxModel [listeners]
  DbComboBoxModel

  (record-count [this] (count records))
  
  (record-at [this index] (nth records index))
  
  (set-list-data-listeners [this list-data-listeners]
    (reset! listeners list-data-listeners))
  
  (remove-list-data-listeners [this list-data-listeners]
    (reset! listeners nil))
  
  (destroy [this]
    (reset! listeners nil)))

(deftype TestListDataListener []
  ListDataListener
  (contentsChanged [this event])
  
  (intervalAdded [this event])
  
  (intervalRemoved [this event]))

(deftest test-create
  (let [listeners (atom nil)
        korma-combobox-model (create (TestDbComboBoxModel. listeners))]
    (is (= (.getElementAt korma-combobox-model 0) foo-record))
    (is (= (.getElementAt korma-combobox-model 1) bar-record))
    (is (= (.getSize korma-combobox-model) 2))
    (is (nil? (.getSelectedItem korma-combobox-model)))
    (.setSelectedItem korma-combobox-model bar-record)
    (is (= (.getSelectedItem korma-combobox-model) bar-record))
    (let [list-data-listener (new TestListDataListener)]
      (is (= (listener-list/count-listeners @listeners) 0))
      (.addListDataListener korma-combobox-model list-data-listener)
      (is (= (listener-list/listeners @listeners) [list-data-listener]))
      (.removeListDataListener korma-combobox-model list-data-listener)
      (is (= (listener-list/count-listeners @listeners) 0)))))

(deftest test-create-list-data-event
  (let [source (new Object)
        type ListDataEvent/CONTENTS_CHANGED
        start-index 0
        end-index 1
        list-data-event (create-list-data-event
                          source type start-index end-index)]
    (is (= (.getSource list-data-event) source))
    (is (= (.getType list-data-event) type))
    (is (= (.getIndex0 list-data-event) start-index))
    (is (= (.getIndex1 list-data-event) end-index))))

(deftest test-create-change-all-list-data-event
  (let [source (new Object)
        end-index 1
        list-data-event (create-change-all-list-data-event source end-index)]
    (is (= (.getSource list-data-event) source))
    (is (= (.getType list-data-event) ListDataEvent/CONTENTS_CHANGED))
    (is (= (.getIndex0 list-data-event) 0))
    (is (= (.getIndex1 list-data-event) end-index))))

(def received-contents-changed-event (atom nil))

(deftype Listener []
  ListDataListener
  (contentsChanged [this event]
    (reset! received-contents-changed-event event))
  
  (intervalAdded [this event])
  
  (intervalRemoved [this event]))

(deftest test-contents-changed
  (let [list-data-event (create-change-all-list-data-event (new Object) 1)] 
    (is (nil? @received-contents-changed-event))
    (contents-changed (new Listener) list-data-event)
    (is (= @received-contents-changed-event list-data-event))
    (reset! received-contents-changed-event nil)))

(deftest test-notify-all-of-contents-changed
  (let [list-data-event (create-change-all-list-data-event (new Object) 1)
        test-listener-list (listener-list/create)]
    (listener-list/add-listener test-listener-list (new Listener))
    (is (nil? @received-contents-changed-event))
    (notify-all-of-contents-changed test-listener-list list-data-event)
    (is (= @received-contents-changed-event list-data-event))
    (reset! received-contents-changed-event nil)))

(deftest test-destroy-model
  (let [listeners (atom nil)
        korma-combobox-model (create (TestDbComboBoxModel. listeners))
        combobox (seesaw-core/combobox :model korma-combobox-model)]
    (.addListDataListener korma-combobox-model (new Listener))
    (is @listeners)
    (destroy-model combobox)
    (is (nil? @listeners))))