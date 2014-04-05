(ns masques.view.friend.my-requests-table-model
  (:require [clj-internationalization.term :as term]
            [masques.model.base :as base-model]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.message :as message-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.view.subviews.korma-table-model :as korma-table-model]
            [masques.view.subviews.table-renderer :as table-renderer]
            [seesaw.core :as seesaw-core]
            [masques.view.utils :as utils])
  (:import [javax.swing.table TableModel]
           [javax.swing ImageIcon]))

(def request-id-key :request-id)

(def columns [{ :id :avatar :text "" :class ImageIcon }
              { :id :alias :text (term/alias) :class String }
              { :id :message :text (term/message) :class String }
              { :id :accept :text "" }
              { :id :reject :text "" }])

(def columns-map (reduce #(assoc %1 (:id %2) %2) {} columns))

(defn find-column
  "Finds the column with the given column-id"
  [column-id]
  (get columns-map column-id))

(deftype MyRequestsTableModel []
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
    (friend-request-model/count-pending-acceptance-requests))
  
  (value-at [this row-index column-id]
    (condp = column-id
      :avatar
        (ImageIcon.
          (ClassLoader/getSystemResource "profile.png"))
      :alias
        (profile-model/alias
          (:profile-id
            (friend-request-model/pending-acceptance-request row-index)))
      :message
        (message-model/body
          (share-model/message-id-key
            (share-model/find-friend-request-share
              (friend-request-model/pending-acceptance-request row-index)
              (base-model/h2-keyword share-model/message-id-key))))
      :accept
        (:id (friend-request-model/pending-request row-index))
      :reject
        (:id (friend-request-model/pending-request row-index))
      nil))
  
  (cell-editable? [this row-index column]
    false)
  
  (update-value [this _ _ _]))

(defn create
  "Creates a new table model for use in the my requests table."
  []
  (korma-table-model/create (new MyRequestsTableModel)))

(defn accept-button-cell-renderer
  "A table cell renderer function for the cancel button."
  [table value isSelected hasFocus row column]
  (let [button (utils/create-link-button :text (term/accept))]
    (utils/save-component-property button request-id-key value)
    button))

(defn reject-button-cell-renderer
  "A table cell renderer function for the cancel button."
  [table value isSelected hasFocus row column]
  (let [button (utils/create-link-button :text (term/reject))]
    (utils/save-component-property button request-id-key value)
    button))