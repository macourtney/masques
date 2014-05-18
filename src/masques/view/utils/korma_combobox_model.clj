(ns masques.view.utils.korma-combobox-model
  (:require [masques.view.utils.listener-list
             :as listener-list]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing ComboBoxModel]
           [javax.swing.event ListDataEvent]))

(defprotocol DbComboBoxModel
  "A model for displaying data to a combobox from a korma db."

  (record-count [this] "Returns the number of records for this model.")
  
  (record-at [this index] "Returns the record at the given index")
  
  (set-list-data-listeners [this list-data-listeners]
    "Sets the list data listeners which should be updated when ever the data in
the model changes.")
  
  (remove-list-data-listeners [this list-data-listeners]
    "Removes the list data listeners.")
  
  (destroy [this]
    "Cleans up anything that needs to be cleaned up when this model will be
destroyed."))

(defprotocol KormaComboBoxModelProtocol
  (destroy-combo-box-model [this]
    "Cleans up anything that needs to be cleaned up when the model is ready to
be destroyed."))

(deftype KormaComboBoxModel [list-data-listeners db-combobox-model
                             selected-element]
  KormaComboBoxModelProtocol
  (destroy-combo-box-model [this]
    (destroy db-combobox-model)
    (doseq [listener (listener-list/listeners list-data-listeners)]
      (listener-list/remove-listener list-data-listeners listener)))
  
  ComboBoxModel
  (getElementAt [this index]
    (record-at db-combobox-model index))
  
  (getSize [this]
    (record-count db-combobox-model))
  
  (setSelectedItem [this record]
    (reset! selected-element record))
  
  (getSelectedItem [this]
    @selected-element)
  
  (addListDataListener [this listener]
    (listener-list/add-listener list-data-listeners listener))
  
  (removeListDataListener [this listener]
    (listener-list/remove-listener list-data-listeners listener)))

(defn create
  "Creates a new KormaComboBoxModel using the given DbComboBoxModel"
  [db-combobox-model]
  (let [list-data-listeners (listener-list/create)]
    (set-list-data-listeners db-combobox-model list-data-listeners)
    (KormaComboBoxModel. list-data-listeners db-combobox-model (atom nil))))

(defn create-list-data-event
  "Creates a new ListDataEvent with the given source type start and end index."
  [source type start-index end-index]
  (ListDataEvent. source type start-index end-index))

(defn create-change-all-list-data-event
  "Creates a change ListDataEvent covering the begining to the given end index."
  [source end-index]
  (create-list-data-event source ListDataEvent/CONTENTS_CHANGED 0 end-index))

(defn contents-changed
  "Notifies the given ListDataListener of the contents changed event."
  [listener list-data-event]
  (.contentsChanged listener list-data-event))

(defn notify-all-of-contents-changed
  "Notifies all of the listeners in the given ListDataListenerList of the
contents changed event."
  [list-data-listeners list-data-event]
  (listener-list/notify-all-listeners
    list-data-listeners #(contents-changed % list-data-event)))

(defn destroy-model
  "Retrieves the KormaComboBoxModel from the given combobox and runs the destroy
function on it."
  [combobox]
  (when-let [combobox-model (seesaw-core/config combobox :model)]
    (when (instance? KormaComboBoxModel combobox-model)
      (destroy-combo-box-model combobox-model))))