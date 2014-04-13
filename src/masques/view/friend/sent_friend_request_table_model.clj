(ns masques.view.friend.sent-friend-request-table-model
  (:require [clj-internationalization.term :as term]
            [masques.model.base :as base-model]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.message :as message-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.view.utils.korma-table-model :as korma-table-model]
            [masques.view.utils :as utils])
  (:import [javax.swing ImageIcon]
           [javax.swing.table TableModel]))

(def request-id-key :request-id)

(def id-key :id)
(def text-key :text)
(def class-key :class)

(def alias-column-id :alias)
(def avatar-column-id :avatar)
(def message-column-id :message)
(def unfriend-column-id :unfriend)

(def columns [{ id-key avatar-column-id text-key "" }
              { id-key alias-column-id text-key (term/alias) class-key String }
              { id-key message-column-id text-key (term/message)
                class-key String }
              { id-key unfriend-column-id text-key "" }])

(def columns-map (reduce #(assoc %1 (id-key %2) %2) {} columns))

(defn find-column
  "Finds the column with the given column-id"
  [column-id]
  (get columns-map column-id))

(deftype SentFriendRequestTableModel []
  korma-table-model/DbModel
  (column-id [this column-index]
    (id-key (nth columns column-index)))

  (column-name [this column-id]
    (text-key (find-column column-id)))
  
  (column-class [this column-id]
    (class-key (find-column column-id)))
  
  (column-count [this]
    (count columns))

  (row-count [this]
    (friend-request-model/count-pending-sent-requests))
  
  (value-at [this row-index column-id]
    (condp = column-id
      avatar-column-id
        (ImageIcon.
          (ClassLoader/getSystemResource "profile.png"))
      alias-column-id
        (profile-model/alias
          (friend-request-model/profile-id-key
            (friend-request-model/pending-sent-request row-index)))
      message-column-id
        (message-model/body
          (share-model/message-id-key
            (share-model/find-friend-request-share
              (friend-request-model/pending-sent-request row-index)
              (base-model/h2-keyword share-model/message-id-key))))
      unfriend-column-id
        (id-key (friend-request-model/pending-sent-request row-index))
      nil))
  
  (cell-editable? [this row-index column-id]
    (= unfriend-column-id column-id))
  
  (update-value [this _ _ _]))

(defn create []
  (korma-table-model/create (new SentFriendRequestTableModel)))

(defn unfriend-button-cell-renderer
  "A table cell renderer function for the cancel button."
  [table value isSelected hasFocus row column]
  (let [unfriend-button (utils/create-link-button :text (term/unfriend))]
    (utils/save-component-property unfriend-button request-id-key value)
    unfriend-button))