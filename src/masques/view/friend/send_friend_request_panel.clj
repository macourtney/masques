(ns masques.view.friend.send-friend-request-panel
  (:require [clj-internationalization.term :as term]
            [clojure.tools.logging :as logging]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.profile :as profile-model]
            [masques.service.calls.request-friendship
             :as request-friendship-call]
            [masques.view.friend.utils :as friend-utils]
            [masques.view.subviews.panel :as panel-subview]
            [masques.view.utils :as view-utils]
            [seesaw.chooser :as seesaw-chooser]
            [seesaw.core :as seesaw-core]))

(def load-masque-file-button-file-key :load-masque-file-button-file)
(def load-masque-file-button-id :load-masque-file-button)
(def load-masque-file-button-listener-key :load-masque-file-button-listener)
(def load-masque-file-text-id :load-masque-file-text)

(def friend-request-message-text-id :friend-request-message-text-id)

(def send-friend-request-cancel-button-id :send-friend-request-cancel-button)
(def send-friend-request-cancel-button-listener-key
  :send-friend-request-cancel-button-listener)

(def send-friend-request-send-button-id :send-friend-request-button-2)
(def send-friend-request-send-button-listener-key
  :send-friend-request-send-button-listener)

(defn create-friend-request-message-text []
  (seesaw-core/scrollable
    (seesaw-core/text :id friend-request-message-text-id
                      :rows 5
                      :multi-line? true
                      :wrap-lines? true)))

(defn create-load-masque-file-panel []
  (seesaw-core/horizontal-panel
    :items [(view-utils/create-link-button :id load-masque-file-button-id
                                :text (term/load-masque-file))
            [:fill-h 3]
            (seesaw-core/text :id load-masque-file-text-id :editable? false)]))

(defn create-send-friend-button-panel []
  (seesaw-core/horizontal-panel
    :items [(view-utils/create-link-button
              :id send-friend-request-send-button-id
              :text (term/send-friend-request)
              :enabled? false)
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
            (create-load-masque-file-panel)
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

(defn find-send-friend-request-send-button
  "Finds the send friend request cancel button in the given view."
  [view]
  (view-utils/find-component view send-friend-request-send-button-id))

(defn find-load-masque-file-button
  "Finds the load mid file button from the given view."
  [view]
  (view-utils/find-component view load-masque-file-button-id))

(defn find-load-masque-file-text
  "Finds the load mid file text field from the given view."
  [view]
  (view-utils/find-component view load-masque-file-text-id))

(defn find-friend-request-message-text
  "Finds the friend request message text in the given view."
  [view]
  (view-utils/find-component view friend-request-message-text-id))

(defn add-action-listener-to-load-masque-file-button
  "Adds the given listener to the mid file button."
  [view listener]
  (view-utils/add-action-listener-to-button
    (find-load-masque-file-button view)
    listener 
    load-masque-file-button-listener-key))

(defn load-masque-file-success
  "Saves the file to a property on the load mid file button and adds the mid 
file text to the text field."
  [view file-chooser file]
  (view-utils/save-component-property
    (find-load-masque-file-button view) load-masque-file-button-file-key file)
  (seesaw-core/config! (find-load-masque-file-text view) :text (.getName file))
  (seesaw-core/config! (find-send-friend-request-send-button view)
                       :enabled? true))

(defn masque-file
  "Finds the mid file saved after loading the mid file."
  [view]
  (view-utils/retrieve-component-property (find-load-masque-file-button view)
                                          load-masque-file-button-file-key))

(defn load-masque-file-listener
  "Opens a file chooser to choose a mid file and loads it into the view for
later importing into the friend request model."
  [event]
  (let [friend-panel-view (friend-utils/find-friend-panel
                            (view-utils/top-level-ancestor
                              (seesaw-core/to-widget event)))]
    (seesaw-chooser/choose-file
      :type :open
      :selection-mode :files-only
      :filters friend-utils/masque-file-filters
      :success-fn (partial load-masque-file-success friend-panel-view))))

(defn attach-load-masque-file-listener
  "Attaches the load mid file listener to the load masque file button."
  [view]
  (add-action-listener-to-load-masque-file-button view load-masque-file-listener))

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

(defn add-action-listener-to-send-friend-request-send-button
  "Adds the given listener to the send friend request send button."
  [view listener]
  (view-utils/add-action-listener-to-button
    (find-send-friend-request-send-button view)
    listener 
    send-friend-request-send-button-listener-key))

(defn send-friend-request-send-listener
  "Opens the friend panel."
  [event]
  (let [friend-panel-view (friend-utils/find-friend-panel
                            (view-utils/top-level-ancestor
                              (seesaw-core/to-widget event)))
        message-text (seesaw-core/text
                       (find-friend-request-message-text friend-panel-view))
        file (masque-file friend-panel-view)]
    (if (friend-request-model/rejected? file)
      (seesaw-core/alert
        (term/sorry-already-rejected
          (profile-model/alias (profile-model/load-masque-file file))))
      (future
        (request-friendship-call/send-friend-request file message-text))))
  (send-friend-request-cancel-listener event))

(defn attach-send-friend-request-send-listener
  "Attaches the send friend request cancel listener to the send friend request
cancel button in the given view."
  [view]
  (add-action-listener-to-send-friend-request-send-button
    view send-friend-request-send-listener))

(defn initialize
  "Initializes the send friend request panel."
  [view]
  (attach-load-masque-file-listener view)
  (attach-send-friend-request-cancel-listener view)
  (attach-send-friend-request-send-listener view))