(ns masques.view.friend.panel
  (:require [clj-internationalization.term :as term]
            [masques.model.profile :as profile-model]
            [masques.view.subviews.panel :as panel-subview]
            [masques.view.utils :as view-utils]
            [seesaw.border :as seesaw-border]
            [seesaw.chooser :as seesaw-chooser]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JOptionPane]))

(def export-mid-button-id :export-mid-button)
(def export-mid-button-listener-key :export-mid-button-listener)

(def send-friend-request-button-id :send-friend-request-button)
(def send-friend-request-button-listener-key
  :send-friend-request-button-listener)

(def send-friend-request-cancel-button-id :send-friend-request-cancel-button)
(def send-friend-request-cancel-button-listener-key
  :send-friend-request-cancel-button-listener)
(def load-mid-file-button-id :load-mid-file-button)
(def load-mid-file-button-listener-key :load-mid-file-button-listener)
(def load-mid-file-button-file-key :load-mid-file-button-file)
(def load-mid-file-text-id :load-mid-file-text)

(def friend-request-message-text-id :friend-request-message-text-id)

(def main-panel-key :main-panel-key)
(def import-friend-panel-key :import-friend-panel-key)

(def card-panel-id "friend-panel")
(def friend-panel-key :friend-panel)
(def show-panel-fn-key :show-panel-fn)

(def mid-file-filters [["Masques Id File" ["mid"]]
                       ["Folders" (fn [file] (.isDirectory file))]])

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

(defn create-pending-friend-requests-table []
  (seesaw-core/scrollable
    (seesaw-core/table :model [:columns [""  :Alias :Message ""]])))

(defn create-pending-friend-requests-tab []
  (seesaw-core/border-panel
    :center (create-pending-friend-requests-table)

    :border 15))

(defn create-body-panel []
  (seesaw-core/tabbed-panel
    :tabs [{ :title (term/all-friends) :content (create-all-friends-tab) }
           { :title (term/pending-friend-requests)
            :content (create-pending-friend-requests-tab) }]))

(defn create-main-panel []
  (seesaw-core/border-panel
    :id "friend-main-panel"

    :north (create-search-panel)
    :center (create-body-panel)

    :vgap 10
    :border 11))

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

(defn create-import-friend-panel []
  (seesaw-core/border-panel
    :id "import-friend-panel"

    :north (create-send-friend-request-body-panel)

    :vgap 10
    :border 11))

(defn find-friend-panel
  "Finds the export mid button in the given view."
  [view]
  (view-utils/find-component view card-panel-id))

(defn show-main-panel [view]
  (seesaw-core/show-card! (find-friend-panel view) main-panel-key)
  view)

(defn show-send-friend-request-panel [view]
  (seesaw-core/show-card! (find-friend-panel view) import-friend-panel-key)
  view)

(defn create [friend-panel]
  (let [friend-panel-view
        (seesaw-core/card-panel
          :id card-panel-id
          :items [[(create-main-panel) main-panel-key]
                  [(create-import-friend-panel) import-friend-panel-key]])]
    (view-utils/save-component-property friend-panel-view friend-panel-key
                                      friend-panel)
    (show-main-panel friend-panel-view)))

(defn find-panel
  "Finds the panel attached to the view."
  [view]
  (view-utils/retrieve-component-property (find-friend-panel view)
                                          friend-panel-key))

(defn find-show-panel-fn
  "Finds the show panel fn attached to the view."
  [view]
  (view-utils/retrieve-component-property (find-friend-panel view)
                                          show-panel-fn-key))

(defn find-export-mid-button
  "Finds the export mid button in the given view."
  [view]
  (view-utils/find-component view export-mid-button-id))

(defn find-send-friend-request-button
  "Finds the send friend request button in the given view."
  [view]
  (view-utils/find-component view send-friend-request-button-id))

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
  (let [friend-panel-view (find-friend-panel (view-utils/top-level-ancestor
                                               (seesaw-core/to-widget event)))]
    (seesaw-chooser/choose-file
      :type :open
      :selection-mode :files-only
      :filters mid-file-filters
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
  (let [friend-panel-view (find-friend-panel (view-utils/top-level-ancestor
                                               (seesaw-core/to-widget event)))]
    ((find-show-panel-fn friend-panel-view) (find-panel friend-panel-view))))

(defn attach-send-friend-request-cancel-listener
  "Attaches the send friend request cancel listener to the send friend request
cancel button in the given view."
  [view]
  (add-action-listener-to-send-friend-request-cancel-button
    view send-friend-request-cancel-listener))

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
                        mid-file-filters))

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
    (find-friend-panel (view-utils/top-level-ancestor
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
  (view-utils/save-component-property (find-friend-panel view) show-panel-fn-key
                                      show-panel-fn)
  (attach-export-mid-listener view)
  (attach-send-friend-request-listener view)
  (attach-send-friend-request-cancel-listener view)
  (attach-load-mid-file-listener view))

(defn show
  "Makes sure the main panel is visible."
  [view args]
  (show-main-panel view))