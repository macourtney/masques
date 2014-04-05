(ns masques.view.friend.my-requests-table-model
  (:require [clj-internationalization.term :as term]
            [masques.model.base :as base-model]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.message :as message-model]
            [masques.model.profile :as profile-model]
            [masques.model.share :as share-model]
            [masques.view.subviews.korma-table-model :as korma-table-model])
  (:import [javax.swing.table TableModel]))

(def columns [{ :text "" }
              { :text (term/alias) :class String }
              { :text (term/message) :class String }
              { :text "" }
              { :text "" }])

(defn find-column [column]
  (some #(when (= column (:text %1)) %1) columns))

(deftype MyRequestsTableModel []
  korma-table-model/DbModel
  (columns [this]
    (map :text columns))
  
  (column-class [this column]
    (:class (find-column column)))
  
  (row-count [this]
    (friend-request-model/count-pending-acceptance-requests))
  
  (value-at [this row-index column]
    (condp = column
      (term/alias)
        (profile-model/alias
          (:profile-id
            (friend-request-model/pending-acceptance-request row-index)))
      (term/message)
        (message-model/body
          (share-model/message-id-key
            (share-model/find-friend-request-share
              (friend-request-model/pending-acceptance-request row-index)
              (base-model/h2-keyword share-model/message-id-key))))
      nil))
  
  (cell-editable? [this row-index column]
    false)
  
  (update-value [this _ _ _]))

(defn create []
  (korma-table-model/create (new MyRequestsTableModel)))