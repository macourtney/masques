(ns masques.view.friend.panel
  (:require [clj-internationalization.term :as term]
            [masques.model.profile :as profile-model]
            [masques.view.friend.send-friend-request-panel 
             :as send-friend-request-panel]
            [masques.view.friend.sent-friend-request-table-model
             :as sent-friend-request-table-model]
            [masques.view.friend.utils :as friend-utils]
            [masques.view.subviews.panel :as panel-subview]
            [masques.view.utils :as view-utils]
            [seesaw.border :as seesaw-border]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JOptionPane]))

(def export-mid-button-id :export-mid-button)
(def export-mid-button-listener-key :export-mid-button-listener)

(def send-friend-request-button-id :send-friend-request-button)
(def send-friend-request-button-listener-key
  :send-friend-request-button-listener)

(def main-panel-key :main-panel-key)
(def send-friend-request-panel-key :send-friend-request-panel-key)

(defn create-search-fields-panel []
  (seesaw-core/vertical-panel
    :items [(seesaw-core/border-panel
              :west (seesaw-core/horizontal-panel
                      :items [(term/friend)
                              [:fill-h 5]
                              (seesaw-core/text :id :alias-search-text
                                                :columns 10)
                              [:fill-h 5]
                              (term/alias-or-nick)])
              :east (view-utils/create-link-button :id :clear-search-button
                                        :text (term/clear-search)))
            (seesaw-core/border-panel
              :west (seesaw-core/horizontal-panel
                      :items [(term/group)
                              [:fill-h 5]
                              (seesaw-core/combobox
                                :id :group-search-combobox
                                :preferred-size  [100 :by 20])])
              :center (seesaw-core/horizontal-panel
                        :items [(term/added)
                                [:fill-h 5]
                                (seesaw-core/text :id :added-to-1-search-text
                                                  :columns 10)
                                [:fill-h 5]
                                (term/to)
                                [:fill-h 5]
                                (seesaw-core/text :id :added-to-2-search-text
                                                  :columns 10)])
              :east (view-utils/create-link-button :id :search-friends-button
                                        :text (term/search-friends))
              :hgap 15)]
    
    :border [15 (seesaw-border/line-border :thickness 1)]))

(defn create-search-panel []
  (seesaw-core/border-panel
    :center (create-search-fields-panel)
    :south (seesaw-core/border-panel
             :west (view-utils/create-link-button :id :advanced-friend-search-button
                                       :text (term/advanced-friend-search)))
    
    :vgap 5))

(defn create-all-friends-table []
  (seesaw-core/scrollable
    (seesaw-core/table :model [:columns [""  :Alias :Nick :Groups ""]])))

(defn create-all-friends-tab []
  (seesaw-core/border-panel
    :north (seesaw-core/border-panel
             :west (view-utils/create-link-button :id send-friend-request-button-id
                                       :text (term/plus-send-friend-request))
             :east (view-utils/create-link-button :id export-mid-button-id
                                       :text (term/export-mid)))
    :center (create-all-friends-table)

    :vgap 5
    :border 15))

(defn create-my-requests-table []
  (seesaw-core/scrollable
    (seesaw-core/table :model [:columns [""  :Alias :Message ""]])))

(defn create-my-requests-tab []
  (seesaw-core/border-panel
    :center (create-my-requests-table)

    :border 15))

(defn create-sent-requests-table []
  (seesaw-core/scrollable
    (seesaw-core/table :model (sent-friend-request-table-model/create))))

(defn create-sent-requests-tab []
  (seesaw-core/border-panel
    :center (create-sent-requests-table)

    :border 15))

(defn create-body-panel []
  (seesaw-core/tabbed-panel
    :tabs [{ :title (term/all-friends) :content (create-all-friends-tab) }
           { :title (term/my-requests) :content (create-my-requests-tab) }
           { :title (term/sent-requests) 
             :content (create-sent-requests-tab) }]))

(defn create-main-panel []
  (seesaw-core/border-panel
    :id "friend-main-panel"

    :north (create-search-panel)
    :center (create-body-panel)

    :vgap 10
    :border 11))

(defn show-main-panel [view]
  (seesaw-core/show-card! (friend-utils/find-friend-panel view) main-panel-key)
  view)

(defn show-send-friend-request-panel [view]
  (seesaw-core/show-card! (friend-utils/find-friend-panel view) send-friend-request-panel-key)
  view)

(defn create [friend-panel]
  (let [friend-panel-view
        (seesaw-core/card-panel
          :id friend-utils/card-panel-id
          :items [[(create-main-panel) main-panel-key]
                  [(send-friend-request-panel/create)
                    send-friend-request-panel-key]])]
    (friend-utils/save-panel friend-panel-view friend-panel)
    (show-main-panel friend-panel-view)))

(defn find-export-mid-button
  "Finds the export mid button in the given view."
  [view]
  (view-utils/find-component view export-mid-button-id))

(defn find-send-friend-request-button
  "Finds the send friend request button in the given view."
  [view]
  (view-utils/find-component view send-friend-request-button-id))

(defn add-action-listener-to-export-mid-button
  "Adds the given action listener to the export mid button."
  [view listener]
  (view-utils/add-action-listener-to-button
    (find-export-mid-button view) listener export-mid-button-listener-key))

(defn export-mid-success
  [file-chooser mid-file]
  (if (.exists mid-file)
    (when (= (term/overwrite)
             (seesaw-core/input (term/file-already-exists-overwrite)
                                :choices [(term/overwrite)
                                          (term/do-not-overwrite)]))
      (profile-model/create-masques-id-file mid-file))
    (profile-model/create-masques-id-file mid-file)))

(defn export-mid-listener
  "The export mid listener which exports the masques id to a directory the user
chooses."
  [event]
  (view-utils/save-file (seesaw-core/to-widget event) export-mid-success
                        friend-utils/mid-file-filters))

(defn attach-export-mid-listener
  "Attaches the export mid listener to the export mid button in the given view."
  [view]
  (add-action-listener-to-export-mid-button view export-mid-listener))

(defn add-action-listener-to-send-friend-request-button
  "Adds the given action listener to the send friend request button."
  [view listener]
  (view-utils/add-action-listener-to-button
    (find-send-friend-request-button view) listener
    send-friend-request-button-listener-key))

(defn send-friend-request-listener
  "Opens the import friend panel."
  [event]
  (show-send-friend-request-panel
    (friend-utils/find-friend-panel (view-utils/top-level-ancestor
                         (seesaw-core/to-widget event)))))

(defn attach-send-friend-request-listener
  "Attaches the send friend request listener to the send friend request button
in the given view."
  [view]
  (add-action-listener-to-send-friend-request-button view
                                              send-friend-request-listener))

(defn initialize
  "Called when the panel is created to initialize the view by attaching
listeners and loading initial data."
  [view show-panel-fn]
  (friend-utils/save-show-panel-fn view show-panel-fn)
  (send-friend-request-panel/initialize view)
  (attach-export-mid-listener view)
  (attach-send-friend-request-listener view))

(defn show
  "Makes sure the main panel is visible."
  [view args]
  (show-main-panel view))