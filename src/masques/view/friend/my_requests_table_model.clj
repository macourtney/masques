(ns masques.view.friend.my-requests-table-model
  (:require [clj-internationalization.term :as term]
            [masques.model.base :as base-model]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.message :as message-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.view.utils.korma-table-model :as korma-table-model]
            [seesaw.core :as seesaw-core]
            [masques.view.utils :as utils])
  (:import [javax.swing.table TableModel]
           [javax.swing ImageIcon]))

(def request-id-key :request-id)

(def accept-column-id :accept)
(def alias-column-id :alias)
(def avatar-column-id :avatar)
(def message-column-id :message)
(def reject-column-id :reject)

(def columns [{ korma-table-model/id-key avatar-column-id
                korma-table-model/text-key ""
                korma-table-model/class-key ImageIcon }
              { korma-table-model/id-key alias-column-id
                korma-table-model/text-key (term/alias)
                korma-table-model/class-key String }
              { korma-table-model/id-key message-column-id
                korma-table-model/text-key (term/message)
                korma-table-model/class-key String }
              { korma-table-model/id-key accept-column-id
                korma-table-model/text-key ""
                korma-table-model/class-key Integer
                korma-table-model/edtiable?-key true }
              { korma-table-model/id-key reject-column-id
                korma-table-model/text-key ""
                korma-table-model/class-key Integer
                korma-table-model/edtiable?-key true }])

(deftype MyRequestsTableModel []

  korma-table-model/ColumnValueList
  (row-count [this]
    (friend-request-model/count-pending-received-requests))
  
  (value-at [this row-index column-id]
    (condp = column-id
      avatar-column-id
        (ImageIcon.
          (ClassLoader/getSystemResource "profile.png"))
      alias-column-id
        (profile-model/alias
          (:profile-id
            (friend-request-model/pending-received-request row-index)))
      message-column-id
        (message-model/body
          (share-model/message-id-key
            (share-model/find-friend-request-share
              (friend-request-model/pending-received-request row-index)
              (base-model/h2-keyword share-model/message-id-key))))
      accept-column-id
        (korma-table-model/id-key
          (friend-request-model/pending-received-request row-index))
      reject-column-id
        (korma-table-model/id-key (friend-request-model/pending-received-request row-index))
      nil))
  
  (update-value [this _ _ _]))

(defn create
  "Creates a new table model for use in the my requests table."
  []
  (korma-table-model/create-from-columns columns (new MyRequestsTableModel)))

(defn accept-button-cell-renderer
  "A table cell renderer function for the accept button."
  [table value isSelected hasFocus row column]
  (let [button (utils/create-link-button :text (term/accept))]
    (utils/save-component-property button request-id-key value)
    button))

(defn reject-button-cell-renderer
  "A table cell renderer function for the reject button."
  [table value isSelected hasFocus row column]
  (let [button (utils/create-link-button :text (term/reject))]
    (utils/save-component-property button request-id-key value)
    button))