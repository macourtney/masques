(ns masques.view.friend.send-friend-request-panel
  (:require [clj-internationalization.term :as term]
            [masques.view.friend.utils :as friend-utils]
            [masques.view.subviews.panel :as panel-subview]
            [masques.view.utils :as view-utils]
            [seesaw.chooser :as seesaw-chooser]
            [seesaw.core :as seesaw-core]))

(def load-mid-file-button-file-key :load-mid-file-button-file)
(def load-mid-file-button-id :load-mid-file-button)
(def load-mid-file-button-listener-key :load-mid-file-button-listener)
(def load-mid-file-text-id :load-mid-file-text)

(def friend-request-message-text-id :friend-request-message-text-id)

(def send-friend-request-cancel-button-id :send-friend-request-cancel-button)
(def send-friend-request-cancel-button-listener-key
  :send-friend-request-cancel-button-listener)

(defn create-friend-request-message-text []
  (seesaw-core/scrollable
    (seesaw-core/text :id friend-request-message-text-id
                      :rows 5
                      :multi-line? true
                      :wrap-lines? true)))

(defn create-load-mid-file-panel []
  (seesaw-core/horizontal-panel
    :items [(view-utils/create-link-button :id load-mid-file-button-id
                                :text (term/load-mid-file))
            [:fill-h 3]
            (seesaw-core/text :id load-mid-file-text-id :editable? false)]))

(defn create-send-friend-button-panel []
  (seesaw-core/horizontal-panel
    :items [(view-utils/create-link-button :id :send-friend-request-button-2
                                :text (term/send-friend-request))
            [:fill-h 25]
            (view-utils/create-link-button
              :id send-friend-request-cancel-button-id
              :text (term/cancel))]))

(defn create-send-friend-request-body-panel []
  (seesaw-core/vertical-panel
    :items [(panel-subview/create-panel-label (term/send-friend-request))
            [:fill-v 5]
            (seesaw-core/border-panel :west (term/message))
            [:fill-v 3]
            (create-friend-request-message-text)
            [:fill-v 5]
            (create-load-mid-file-panel)
            [:fill-v 5]
            (create-send-friend-button-panel)]))

(defn create []
  (seesaw-core/border-panel
    :id "import-friend-panel"

    :north (create-send-friend-request-body-panel)

    :vgap 10
    :border 11))

(defn find-send-friend-request-cancel-button
  "Finds the send friend request cancel button in the given view."
  [view]
  (view-utils/find-component view send-friend-request-cancel-button-id))

(defn find-load-mid-file-button
  "Finds the load mid file button from the given view."
  [view]
  (view-utils/find-component view load-mid-file-button-id))

(defn find-load-mid-file-text
  "Finds the load mid file text field from the given view."
  [view]
  (view-utils/find-component view load-mid-file-text-id))

(defn add-action-listener-to-load-mid-file-button
  "Adds the given listener to the mid file button."
  [view listener]
  (view-utils/add-action-listener-to-button
    (find-load-mid-file-button view)
    listener 
    load-mid-file-button-listener-key))

(defn load-mid-file-success
  "Saves the file to a property on the load mid file button and adds the mid 
file text to the text field."
  [view file-chooser file]
  (view-utils/save-component-property
    (find-load-mid-file-button view) load-mid-file-button-file-key file)
  (seesaw-core/config! (find-load-mid-file-text view) :text (.getName file)))

(defn load-mid-file-listener
  "Opens a file chooser to choose a mid file and loads it into the view for
later importing into the friend request model."
  [event]
  (let [friend-panel-view (friend-utils/find-friend-panel
                            (view-utils/top-level-ancestor
                              (seesaw-core/to-widget event)))]
    (seesaw-chooser/choose-file
      :type :open
      :selection-mode :files-only
      :filters friend-utils/mid-file-filters
      :success-fn (partial load-mid-file-success friend-panel-view))))

(defn attach-load-mid-file-listener
  "Attaches the load mid file listener to the load mid file button."
  [view]
  (add-action-listener-to-load-mid-file-button view load-mid-file-listener))

(defn add-action-listener-to-send-friend-request-cancel-button
  "Adds the given listener to the send friend request cancel button."
  [view listener]
  (view-utils/add-action-listener-to-button
    (find-send-friend-request-cancel-button view)
    listener 
    send-friend-request-cancel-button-listener-key))

(defn send-friend-request-cancel-listener
  "Opens the friend panel."
  [event]
  (let [friend-panel-view (friend-utils/find-friend-panel
                            (view-utils/top-level-ancestor
                              (seesaw-core/to-widget event)))]
    ((friend-utils/find-show-panel-fn friend-panel-view)
      (friend-utils/find-panel friend-panel-view))))

(defn attach-send-friend-request-cancel-listener
  "Attaches the send friend request cancel listener to the send friend request
cancel button in the given view."
  [view]
  (add-action-listener-to-send-friend-request-cancel-button
    view send-friend-request-cancel-listener))

(defn initialize
  "Initializes the send friend request panel."
  [view]
  (attach-send-friend-request-cancel-listener view)
  (attach-load-mid-file-listener view))