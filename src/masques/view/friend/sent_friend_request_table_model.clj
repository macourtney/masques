(ns masques.view.friend.sent-friend-request-table-model
  (:require [clj-internationalization.term :as term]
            [masques.model.base :as base-model]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.message :as message-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model])
  (:import [javax.swing.table TableModel]))

(def columns [{ :text "" }
              { :text (term/alias) :class String }
              { :text (term/message) :class String }
              { :text "" }
              { :text "" }])

(deftype SentFriendRequestTableModel [table-model-listener-set]
  TableModel
  (addTableModelListener [this listener]
    (reset! table-model-listener-set (conj @table-model-listener-set listener)))
  
  (getColumnClass [this column-index]
    (or (:class (nth columns column-index)) Object))
  
  (getColumnCount [this]
    (count columns))
  
  (getColumnName [this column-index]
    (:text (nth columns column-index)))
  
  (getRowCount [this]
    (friend-request-model/count-pending-requests))
  
  (getValueAt [this row-index column-index]
    (condp = column-index
      1 (profile-model/alias
          (:profile-id (friend-request-model/pending-request row-index)))
      2 (message-model/body
          (share-model/message-id-key
            (share-model/find-friend-request-share
              (friend-request-model/pending-request row-index)
              (base-model/h2-keyword share-model/message-id-key))))
      nil))
  
  (isCellEditable [this _ _]
    false)
  
  (removeTableModelListener [this listener]
    (reset! table-model-listener-set (disj @table-model-listener-set listener)))
  
  (setValueAt [this _ _ _]))

(defn create []
  (SentFriendRequestTableModel. (atom #{})))