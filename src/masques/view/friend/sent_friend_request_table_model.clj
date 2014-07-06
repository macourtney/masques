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

(def alias-column-id :alias)
(def avatar-column-id :avatar)
(def message-column-id :message)
(def unfriend-column-id :unfriend)

(def columns [{ korma-table-model/id-key avatar-column-id
                korma-table-model/text-key ""
                korma-table-model/class-key Integer }
              { korma-table-model/id-key alias-column-id
                korma-table-model/text-key (term/alias)
                korma-table-model/class-key String }
              { korma-table-model/id-key message-column-id
                korma-table-model/text-key (term/message)
                korma-table-model/class-key String }
              { korma-table-model/id-key unfriend-column-id
                korma-table-model/text-key ""
                korma-table-model/class-key Integer
                korma-table-model/edtiable?-key true }])

(deftype SentFriendRequestTableModel [table-data-listeners interceptor-manager]

  korma-table-model/TableDbModel
  (db-entity [this]
    base-model/friend-request)

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
        (korma-table-model/id-key (friend-request-model/pending-sent-request row-index))
      nil))
  
  (update-value [this _ _ _])
  
  (index-of [this record-or-id]
    (friend-request-model/pending-sent-request-index record-or-id)))

(defn create []
  (korma-table-model/create-from-columns
    columns (SentFriendRequestTableModel. (atom nil) (atom nil))))

(defn unfriend-button-cell-renderer
  "A table cell renderer function for the unfriend button."
  [table value isSelected hasFocus row column]
  (let [unfriend-button (utils/create-link-button :text (term/unfriend))]
    (utils/save-component-property unfriend-button request-id-key value)
    unfriend-button))