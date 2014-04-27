(ns masques.view.friend.all-friends-table-model
  (:require [clj-internationalization.term :as term]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.profile :as profile-model]
            [masques.view.utils.korma-table-model :as korma-table-model]
            [masques.view.utils :as utils])
  (:import [javax.swing ImageIcon]))

(def request-id-key :request-id)

(def columns [{ :id :avatar :text "" :class ImageIcon }
              { :id :alias :text (term/alias) :class String }
              { :id :nick :text (term/nick) :class String }
              { :id :groups :text (term/groups) :class String }
              { :id :details :text "" :class Integer }
              { :id :shares :text "" :class Integer }
              { :id :unfriend :text "" :class Integer }])

(def columns-map (reduce #(assoc %1 (:id %2) %2) {} columns))

(defn find-column
  "Finds the column with the given column-id"
  [column-id]
  (get columns-map column-id))

(deftype FriendsTableModel []
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
    (friend-request-model/count-friends))
  
  (value-at [this row-index column-id]
    (condp = column-id
      :avatar
        (ImageIcon.
          (ClassLoader/getSystemResource "profile.png"))
      :alias
        (profile-model/alias
          (:profile-id
            (friend-request-model/approved-received-request row-index)))
      :nick ""
      :groups ""
      :details
        (:id (friend-request-model/approved-received-request row-index))
      :shares
        (:id (friend-request-model/approved-received-request row-index))
      :unfriend
        (:id (friend-request-model/approved-received-request row-index))
      nil))
  
  (cell-editable? [this row-index column-id]
    (contains? #{ :details :shares :unfriend } column-id ))
  
  (update-value [this _ _ _]))

(defn create
  "Creates a new table model for use in the friends table."
  []
  (korma-table-model/create (new FriendsTableModel)))

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