(ns masques.view.friend.all-friends-table-model
  (:require [clj-internationalization.term :as term]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.profile :as profile-model]
            [masques.view.utils.korma-table-model :as korma-table-model]
            [masques.view.utils :as utils])
  (:import [javax.swing ImageIcon]))

(def request-id-key :request-id)

(def alias-column-id :alias)
(def avatar-column-id :avatar)
(def details-column-id :details)
(def groups-column-id :groups)
(def nick-column-id :nick)
(def shares-column-id :shares)
(def unfriend-column-id :unfriend)

(def columns [{ korma-table-model/id-key avatar-column-id
                korma-table-model/text-key ""
                korma-table-model/class-key ImageIcon }
              { korma-table-model/id-key alias-column-id
                korma-table-model/text-key (term/alias)
                korma-table-model/class-key String }
              { korma-table-model/id-key nick-column-id
                korma-table-model/text-key (term/nick)
                korma-table-model/class-key String }
              { korma-table-model/id-key groups-column-id
                korma-table-model/text-key (term/groups)
                korma-table-model/class-key String }
              { korma-table-model/id-key details-column-id
                korma-table-model/text-key ""
                korma-table-model/class-key Integer
                korma-table-model/edtiable?-key true }
              { korma-table-model/id-key shares-column-id
                korma-table-model/text-key ""
                korma-table-model/class-key Integer
                korma-table-model/edtiable?-key true }
              { korma-table-model/id-key unfriend-column-id
                korma-table-model/text-key ""
                korma-table-model/class-key Integer
                korma-table-model/edtiable?-key true }])

(deftype FriendsTableModel []

  korma-table-model/ColumnValueList
  (row-count [this]
    (friend-request-model/count-friends))
  
  (value-at [this row-index column-id]
    (condp = column-id
      avatar-column-id
        (ImageIcon.
          (ClassLoader/getSystemResource "profile.png"))
      alias-column-id
        (profile-model/alias
          (:profile-id
            (friend-request-model/approved-received-request row-index)))
      nick-column-id ""
      groups-column-id ""
      details-column-id
        (korma-table-model/id-key
          (friend-request-model/approved-received-request row-index))
      shares-column-id
        (korma-table-model/id-key
          (friend-request-model/approved-received-request row-index))
      unfriend-column-id
        (korma-table-model/id-key
          (friend-request-model/approved-received-request row-index))
      nil))
  
  (update-value [this _ _ _]))

(defn create
  "Creates a new table model for use in the friends table."
  []
  (korma-table-model/create-from-columns columns (new FriendsTableModel)))

(defn profile-button-cell-renderer
  "A table cell renderer function for the details button."
  [table value isSelected hasFocus row column]
  (let [button (utils/create-under-construction-link-button
                 :text (term/profile))]
    (utils/save-component-property button request-id-key value)
    button))

(defn shares-button-cell-renderer
  "A table cell renderer function for the shares button."
  [table value isSelected hasFocus row column]
  (let [button (utils/create-under-construction-link-button
                 :text (term/shares))]
    (utils/save-component-property button request-id-key value)
    button))

(defn unfriend-button-cell-renderer
  "A table cell renderer function for the unfriend button."
  [table value isSelected hasFocus row column]
  (let [button (utils/create-link-button :text (term/unfriend))]
    (utils/save-component-property button request-id-key value)
    button))