(ns masques.view.stream.stream-list-model
  (:require [masques.model.base :as base-model]
            [masques.model.share :as share-model]
            [masques.model.share-profile :as share-profile-model]
            [masques.view.utils.korma-list-model :as korma-list-model]))

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

(defn create
  "Creates a new list model for use in the stream list."
  []
  (korma-list-model/create (StreamListModel. (atom nil) (atom nil))))