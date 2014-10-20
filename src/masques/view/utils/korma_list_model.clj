(ns masques.view.utils.korma-list-model
  (:require [clojure.tools.logging :as logging]
            [masques.model.base :as model-base]
            [masques.view.utils.listener-list :as listener-list]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing ListModel]
           [javax.swing.event ListDataEvent ListDataListener]))

;*******************************************************************************
; List protocols.
;*******************************************************************************

(defprotocol ListDbModel
  "An interface for getting the items for a list."
  (db-entity [this]
    "Returns the entity this list db model uses and listen to.")
  
  (size [this]
    "Returns the number of values for this list.")
  
  (element-at [this index]
     "Returns the value at the given index.")
  
  (index-of [this record-or-id]
    "Returns the index of the given record or id in the model. If a record is
passed in, the record must have an id. If the record or id is not in this model,
then nil is retured."))

(defprotocol ListDBListeners
  "Handles all of the listeners for a list."
  
  (add-listener [this listener]
    "Adds the given listener to this list db listeners")
  
  (remove-listener [this listener]
    "Removes the given listener from this list db listeners")
  
  (listener-list [this]
    "Returns the listener list in this list db listeners")
  
  (destroy [this]
    "Cleans up anything that needs to be cleaned up when this model is
destroyed.")
  
  (initialize-listeners [this list-model]
    "Sets the list-model and performs any initialization necessary."))

(defprotocol KormaListModelProtocol
  (destroy-list-model [this]
    "Cleans up anything that needs to be cleaned up when the model is ready to
be destroyed.")
  
  (list-db-model [this]
    "Returns the list db model associated with this list model.")
  
  (list-db-listeners [this]
    "Returns the list db listeners associated with this list model."))

;*******************************************************************************
; List interceptors
;*******************************************************************************

(defn list-interval-added
  "Notifies the given listener of the list interval added event."
  [listener list-model-event]
  (.intervalAdded listener list-model-event))

(defn list-interval-changed
  "Notifies the given listener of the list interval changed event."
  [listener list-model-event]
  (.contentsChanged listener list-model-event))

(defn list-interval-removed
  "Notifies the given listener of the list interval removed event."
  [listener list-model-event]
  (.intervalRemoved listener list-model-event))

(defn create-list-model-event
  "Creates a new ListDataEvent with the given source type start and end index."
  [source type start-index end-index]
  (ListDataEvent. source type start-index end-index))

(defn notify-all
  "Notifies all of the listeners in the given ListDataListenerList using the
given listener-fn."
  [list-data-listeners listener-fn list-model-event]
  (seesaw-core/invoke-later
    (listener-list/notify-all-listeners
      list-data-listeners #(listener-fn % list-model-event))))

(defn notify-model-of-insert
  "Notifies all of the listeners in the given ListModel that the record
with the given id has been added."
  [list-model id]
  (when list-model
    (when-let [record-index (index-of (list-db-model list-model) id)]
      (notify-all
        (listener-list (list-db-listeners list-model))
        list-interval-added
        (create-list-model-event list-model ListDataEvent/INTERVAL_ADDED
                                 record-index record-index)))))

(defn notify-model-of-update
  "Notifies all of the listeners in the given ListModel that the record
with the given id has been updated."
  [list-model id]
  (when list-model
    (when-let [record-index (index-of (list-db-model list-model) id)]
      (notify-all
        (listener-list (list-db-listeners list-model))
        list-interval-changed
        (create-list-model-event list-model ListDataEvent/CONTENTS_CHANGED
                                 record-index record-index)))))

(defn notify-model-of-delete
  "Notifies all of the listeners in the given ListModel that the record with the
given id has been added."
  [list-model record-index]
  (when (and list-model record-index)
    (notify-all
      (listener-list (list-db-listeners list-model))
      list-interval-removed
      (create-list-model-event list-model ListDataEvent/INTERVAL_REMOVED
                               record-index record-index))))

(deftype ListInterceptors [entity list-model]
  model-base/InterceptorProtocol
  (interceptor-entity [this]
    entity)
  
  (create-insert-interceptor [this]
    (fn [action record]
      (let [record-id (action record)]
        (notify-model-of-insert list-model record-id)
        record-id)))
  
  (create-update-interceptor [this]
    (fn [action record]
      (let [record-id (action record)]
        (notify-model-of-update list-model record-id)
        record-id)))
  
  (create-delete-interceptor [this]
    (fn [action record]
      (let [record-index (index-of (list-db-model list-model) record)
            output (action record)]
        (notify-model-of-delete list-model record-index)
        output)))
  
  (create-change-interceptor [this]
    nil))

(defn create-list-interceptors
  "Creates a new ListInterceptors with the given entity and list db model."
  [entity list-model]
  (ListInterceptors. entity list-model))

(deftype KormaListDbListeners
  [db-model list-model list-data-listeners interceptor-manager]

  ListDBListeners
  (add-listener [this listener]
    (listener-list/add-listener list-data-listeners listener))
  
  (remove-listener [this listener]
    (listener-list/remove-listener list-data-listeners listener))
  
  (listener-list [this]
    list-data-listeners)

  (destroy [this]
    (when @interceptor-manager
      (model-base/remove-interceptors @interceptor-manager)
      (reset! interceptor-manager nil))
    (when list-data-listeners
      (doseq [listener (listener-list/listeners list-data-listeners)]
        (listener-list/remove-listener list-data-listeners listener))))
  
  (initialize-listeners [this new-list-model]
    (reset! list-model new-list-model)
    (when new-list-model
      (reset!
        interceptor-manager
        (model-base/create-interceptor-manager
          (create-list-interceptors (db-entity db-model) new-list-model))))))

(deftype KormaListModel [db-model listener-model]

  KormaListModelProtocol
  (destroy-list-model [this]
    (destroy listener-model))
  
  (list-db-model [this]
    db-model)
  
  (list-db-listeners [this]
    listener-model)
  
  ListModel
  (getSize [this]
    (size db-model))
  
  (getElementAt [this index]
    (element-at db-model index))
  
  (removeListDataListener [this listener]
    (remove-listener listener-model listener))
  
  (addListDataListener [this listener]
    (add-listener listener-model listener)))

(defn create
  "Creates a new korma list model with the given db model."
  ([db-model]
    (create db-model
            (KormaListDbListeners.
              db-model (atom nil) (listener-list/create) (atom nil))))
  ([db-model list-db-listeners]
    (let [list-model (KormaListModel. db-model list-db-listeners)]
      (initialize-listeners list-db-listeners list-model)
      list-model)))

(defn destroy-model
  "Retrieves the KormaListModel from the given list and runs the destroy
function on it."
  [list]
  (when-let [list-model (seesaw-core/config list :model)]
    (when (instance? KormaListModel list-model)
      (destroy-list-model list-model))))