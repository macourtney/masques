(ns masques.view.stream.stream-list-model
  (:require [masques.model.base :as base-model]
            [masques.model.share :as share-model]
            [masques.model.share-profile :as share-profile-model]
            [masques.view.utils.korma-list-model :as korma-list-model]
            [masques.view.utils.listener-list :as listener-list]))

(deftype StreamListModel [table-data-listeners interceptor-manager]

  korma-list-model/ListDbModel
  (db-entity [this]
    base-model/share)
  
  (size [this]
    (share-model/count-stream-shares))
  
  (element-at [this index]
    (share-model/find-stream-share-at index))
  
  (index-of [this share-record-or-id]
    (share-model/index-of-stream-share share-record-or-id)))

(deftype ShareInterceptors [list-model]
  base-model/InterceptorProtocol
  (interceptor-entity [this]
    base-model/share)
  
  (create-insert-interceptor [this]
    nil)
  
  (create-update-interceptor [this]
    (fn [action record]
      (let [record-id (action record)]
        (korma-list-model/notify-model-of-update list-model record-id)
        record-id)))
  
  (create-delete-interceptor [this]
    nil)
  
  (create-change-interceptor [this]
    nil))

(deftype ShareProfileInterceptors [list-model]
  base-model/InterceptorProtocol
  (interceptor-entity [this]
    base-model/share-profile)
  
  (create-insert-interceptor [this]
    (fn [action record]
      (let [share-profile-id (action record)
            record-id (share-profile-model/share-id share-profile-id)]
        (korma-list-model/notify-model-of-insert list-model record-id)
        record-id)))
  
  (create-update-interceptor [this]
    (fn [action record]
      (let [share-profile-id (action record)
            record-id (share-profile-model/share-id share-profile-id)]
        (korma-list-model/notify-model-of-update list-model record-id)
        record-id)))
  
  (create-delete-interceptor [this]
    (fn [action record]
      (let [record-id (share-profile-model/share-id record)
            record-index (korma-list-model/index-of
                           (korma-list-model/list-db-model list-model)
                           record-id)
            output (action record)]
        (korma-list-model/notify-model-of-delete list-model record-index)
        output)))
  
  (create-change-interceptor [this]
    nil))

(deftype StreamListDbListeners
  [db-model list-data-listeners interceptor-managers]

  korma-list-model/ListDBListeners
  (add-listener [this listener]
    (listener-list/add-listener list-data-listeners listener))
  
  (remove-listener [this listener]
    (listener-list/remove-listener list-data-listeners listener))
  
  (listener-list [this]
    list-data-listeners)

  (destroy [this]
    (when @interceptor-managers
      (doseq [interceptor-manager @interceptor-managers]
        (base-model/remove-interceptors interceptor-manager))
      (reset! interceptor-managers nil))
    (when list-data-listeners
      (doseq [listener (listener-list/listeners list-data-listeners)]
        (listener-list/remove-listener list-data-listeners listener))))
  
  (initialize-listeners [this list-model]
    (when list-model
      (reset!
        interceptor-managers
        [(base-model/create-interceptor-manager (ShareInterceptors. list-model))
         (base-model/create-interceptor-manager
           (ShareProfileInterceptors. list-model))]))))

(defn create
  "Creates a new list model for use in the stream list."
  []
  (let [db-model (StreamListModel. (atom nil) (atom nil))]
    (korma-list-model/create
      db-model
      (StreamListDbListeners. db-model (listener-list/create) (atom nil)))))