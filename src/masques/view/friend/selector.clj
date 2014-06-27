(ns masques.view.friend.selector
  (:require [clj-internationalization.term :as term]
            [clojure.tools.logging :as logging]
            [masques.model.friend-request :as friend-request-model]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(def panel-id :friend-selector-panel)

(def table-id :friend-selector-table-id)
(def cancel-button-id :friend-selector-cancel-button)

(def table-model-columns [:alias])

(defn create-buttons [submit-text]
  (seesaw-core/border-panel
    :east (seesaw-core/horizontal-panel
            :items [(view-utils/create-under-construction-link-button
                      :id :add-member-button
                      :text submit-text)
                    (view-utils/create-link-button
                      :id cancel-button-id
                      :text (term/cancel))])))

(defn create-table []
  (seesaw-core/scrollable
    (seesaw-core/table
      :id table-id
      :model [:columns table-model-columns])))

(defn create [submit-text]
  (seesaw-core/border-panel
    :id panel-id

    :center (create-table)
    :south (create-buttons submit-text)

    :vgap 5
    :border 11))

(defprotocol FriendSelector
  "A protocol for interfacing with this selector."
  (cancel-selection [this] "Called when the selection is cancelled.")
  
  (submit-selection [this friend] "Called when the user has made a selection."))

(defn find-panel
  "Finds the friend selector panel in the given view."
  [view]
  (view-utils/find-component view panel-id))

(defn find-table
  "Finds the friend selector table in the given view."
  [view]
  (view-utils/find-component view table-id))

(defn find-cancel-button
  "Finds the cancel button in the given view."
  [view]
  (view-utils/find-component view cancel-button-id))

(defn attach-cancel-listener
  "Attaches the add member listener to the add member button in the given
view."
  [view friend-selector]
  (view-utils/add-action-listener-to-button
    (find-cancel-button view)
    (fn [_] (cancel-selection friend-selector))))

(defn initialize
  "Initializes the friend selector panel."
  [view friend-selector]
  (attach-cancel-listener view friend-selector))

(defn load-data
  "Loads the selector with data."
  [view]
  (let [selector-panel (find-panel view)
        friends (friend-request-model/find-all-friends-for-table)]
    (logging/info "friends:" friends)
    (seesaw-core/config!
      (find-table selector-panel)
      :model [:columns table-model-columns
              :rows friends])))