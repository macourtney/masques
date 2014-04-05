(ns masques.view.friend.sent-friend-request-table-model
  (:require [clj-internationalization.term :as term]
            [masques.model.base :as base-model]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.message :as message-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.view.subviews.korma-table-model :as korma-table-model]
            [masques.view.utils :as utils])
  (:import [javax.swing ImageIcon]
           [javax.swing.table TableModel]))

(def request-id-key :request-id)

(def columns [{ :id :avatar :text "" }
              { :id :alias :text (term/alias) :class String }
              { :id :message :text (term/message) :class String }
              { :id :cancel :text "" }])

(def columns-map (reduce #(assoc %1 (:id %2) %2) {} columns))

(defn find-column
  "Finds the column with the given column-id"
  [column-id]
  (get columns-map column-id))

(deftype SentFriendRequestTableModel []
  korma-table-model/DbModel
  (column-id [this column-index]
    (:id (nth columns column-index)))

  (column-name [this column-id]
    (:text (find-column column-id)))
  
  (column-class [this column-id]
    (:class (find-column column-id)))
  
  (column-count [this]
    (count columns))

  (row-count [this]
    (friend-request-model/count-pending-requests))
  
  (value-at [this row-index column-id]
    (condp = column-id
      :avatar
        (ImageIcon.
          (ClassLoader/getSystemResource "profile.png"))
      :alias
        (profile-model/alias
          (:profile-id (friend-request-model/pending-request row-index)))
      :message
        (message-model/body
          (share-model/message-id-key
            (share-model/find-friend-request-share
              (friend-request-model/pending-request row-index)
              (base-model/h2-keyword share-model/message-id-key))))
      :cancel
        (:id (friend-request-model/pending-request row-index))
      nil))
  
  (cell-editable? [this _ _]
    false)
  
  (update-value [this _ _ _]))

(defn create []
  (korma-table-model/create (new SentFriendRequestTableModel)))

(defn cancel-button-cell-renderer
  "A table cell renderer function for the cancel button."
  [table value isSelected hasFocus row column]
  (let [cancel-button (utils/create-link-button :text (term/cancel))]
    (utils/save-component-property cancel-button request-id-key value)
    cancel-button))