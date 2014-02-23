(ns masques.view.friend.panel
  (:require [clj-internationalization.term :as term]
            [masques.model.profile :as profile-model]
            [masques.view.utils :as view-utils]
            [seesaw.border :as seesaw-border]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JOptionPane]))

(def export-mid-button-id :export-mid-button)
(def export-mid-button-listener-key :export-mid-button-listener)

(def link-button-font { :name "DIALOG" :style :plain :size 12 })

(def mid-file-filters [["Masques Id File" ["mid"]]
                       ["Folders" (fn [file] (.isDirectory file))]])

(defn create-link-button [id text]
  (seesaw-core/button :id id :text text :border 0 :font link-button-font))

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
              :east (create-link-button
                      :clear-search-button (term/clear-search)))
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
              :east (create-link-button
                      :search-friends-button (term/search-friends))
              :hgap 15)]
    
    :border [15 (seesaw-border/line-border :thickness 1)]))

(defn create-search-panel []
  (seesaw-core/border-panel
    :center (create-search-fields-panel)
    :south (seesaw-core/border-panel
             :west (create-link-button :advanced-friend-search-button
                                       (term/advanced-friend-search)))
    
    :vgap 5))

(defn create-all-friends-table []
  (seesaw-core/scrollable
    (seesaw-core/table :model [:columns [""  :Alias :Nick :Groups ""]])))

(defn create-all-friends-tab []
  (seesaw-core/border-panel
    :north (seesaw-core/border-panel
             :west (create-link-button :send-friend-request-button
                                       (term/send-friend-request))
             :east (create-link-button export-mid-button-id (term/export-mid)))
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
           { :title (term/pending-friend-requests) :content (create-pending-friend-requests-tab) }]))

(defn create []
  (seesaw-core/border-panel
    :id "friend-panel"

    :north (create-search-panel)
    :center (create-body-panel)

    :vgap 10
    :border 11))

(defn find-export-mid-button
  "Finds the export mid button in the given view."
  [view]
  (view-utils/find-component view export-mid-button-id))

(defn add-action-listener-to-export-mid-button
  "Adds the given action listener to the export mid button."
  [view listener]
  (let [export-mid-button (find-export-mid-button view)
        listener-remover (seesaw-core/listen export-mid-button :action-performed listener)]
    (view-utils/save-component-property export-mid-button
                                        export-mid-button-listener-key
                                        listener-remover)))

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
  "attaches the export mid listener to the export mid button in the given view."
  [view]
  (add-action-listener-to-export-mid-button view export-mid-listener))

(defn initialize
  "Called when the panel is created to initialize the view by attaching
listeners and loading initial data."
  [view]
  (attach-export-mid-listener view))