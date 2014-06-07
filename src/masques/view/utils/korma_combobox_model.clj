(ns masques.view.utils.korma-combobox-model
  (:require [clojure.tools.logging :as logging]
            [masques.view.utils.listener-list :as listener-list]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing ComboBoxModel]
           [javax.swing.event ListDataEvent]))

(defprotocol DbComboBoxModel
  "A model for displaying data to a combobox from a korma db."

  (record-count [this] "Returns the number of records for this model.")
  
  (record-at [this index] "Returns the record at the given index")
  
  (index-of [this record-or-id]
    "Returns the index of the given record or id in the model. If a record is
passed in, the record must have an id. If the record or id is not in this model,
then nil is retured.")
  
  (set-list-data-listeners [this list-data-listeners]
    "Sets the list data listeners which should be updated when ever the data in
the model changes.")
  
  (remove-list-data-listeners [this list-data-listeners]
    "Removes the list data listeners.")
  
  (list-data-listeners [this]
    "Returns the list data listeners in this model.")
  
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

(defn interval-added
  "Notifies the given ListDataListener of the interval added event."
  [listener list-data-event]
  (.intervalAdded listener list-data-event))

(defn contents-changed
  "Notifies the given ListDataListener of the contents changed event."
  [listener list-data-event]
  (.contentsChanged listener list-data-event))

(defn interval-removed
  "Notifies the given ListDataListener of the interval removed event."
  [listener list-data-event]
  (.intervalRemoved listener list-data-event))

(defn notify-all-of-interval-added
  "Notifies all of the listeners in the given ListDataListenerList of the
interval added event."
  [list-data-listeners list-data-event]
  (listener-list/notify-all-listeners
    list-data-listeners #(interval-added % list-data-event)))

(defn notify-all-of-contents-changed
  "Notifies all of the listeners in the given ListDataListenerList of the
contents changed event."
  [list-data-listeners list-data-event]
  (listener-list/notify-all-listeners
    list-data-listeners #(contents-changed % list-data-event)))

(defn notify-all-of-interval-removed
  "Notifies all of the listeners in the given ListDataListenerList of the
interval removed event."
  [list-data-listeners list-data-event]
  (listener-list/notify-all-listeners
    list-data-listeners #(interval-removed % list-data-event)))

(defn notify-model-of-update
  "Notifies all of the listeners in the given DbComboBoxModel that the record
with the given id has been editted."
  [db-combo-box-model id]
  (when db-combo-box-model
    (when-let [record-index (index-of db-combo-box-model id)]
      (notify-all-of-contents-changed
        (list-data-listeners db-combo-box-model)
        (create-list-data-event
          db-combo-box-model ListDataEvent/CONTENTS_CHANGED record-index
          record-index)))))

(defn notify-model-of-insert
  "Notifies all of the listeners in the given DbComboBoxModel that the record
with the given id has been added."
  [db-combo-box-model id]
  (when db-combo-box-model
    (when-let [record-index (index-of db-combo-box-model id)]
      (notify-all-of-interval-added
        (list-data-listeners db-combo-box-model)
        (create-list-data-event
          db-combo-box-model ListDataEvent/INTERVAL_ADDED record-index
          record-index)))))

(defn notify-model-of-delete
  "Notifies all of the listeners in the given DbComboBoxModel that the record
with the given id has been added."
  [db-combo-box-model record-index]
  (when (and db-combo-box-model record-index)
    (notify-all-of-interval-removed
      (list-data-listeners db-combo-box-model)
      (create-list-data-event db-combo-box-model ListDataEvent/INTERVAL_REMOVED
        record-index record-index))))

(defn destroy-model
  "Retrieves the KormaComboBoxModel from the given combobox and runs the destroy
function on it."
  [combobox]
  (when-let [combobox-model (seesaw-core/config combobox :model)]
    (when (instance? KormaComboBoxModel combobox-model)
      (destroy-combo-box-model combobox-model))))