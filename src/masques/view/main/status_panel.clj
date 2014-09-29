(ns masques.view.main.status-panel
  (:require [clj-internationalization.term :as term]
            [clojure.tools.logging :as logging]
            [masques.service.calls.send-status :as send-status-call]
            [masques.view.utils :as view-utils]
            [seesaw.border :as seesaw-border]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]))

(def panel-width 250)

(def status-background-color (seesaw-color/color 238 238 238))
(def button-font { :name "DIALOG" :style :plain :size 10 })

(def title-color (seesaw-color/color 100 100 100))
(def title-font { :name "DIALOG" :style :bold :size 14 })

(def status-panel-id :status-panel)

(def status-text-id :status-text)

(def update-status-button-id :update-status-button)
(def update-status-button-listener-key :update-status-button-listener)

(defn under-construction-button
  "Creates a link button with a font for the status panel."
  [id text]
  (view-utils/create-under-construction-link-button
    :id id :text text :font button-font))

(defn link-button
  "Creates a link button with a font for the status panel."
  [id text]
  (view-utils/create-link-button :id id :text text :font button-font))

(defn create-update-status []
  (seesaw-core/border-panel
    :north
      (seesaw-core/scrollable
        (seesaw-core/text
          :id status-text-id :multi-line? true :wrap-lines? true :rows 4)
        :preferred-size [panel-width :by 75])
    :south
      (seesaw-core/border-panel
        :west (link-button update-status-button-id (term/update-status))
        :east (under-construction-button
                :create-new-share-button (term/create-new-share))
        :hgap 3) 
    :vgap 3))
  
(defn create-recent-shares []
  (seesaw-core/border-panel
    :north (seesaw-core/border-panel
             :west (seesaw-core/label :text (term/recent-shares)
                                      :foreground title-color :font title-font)
             :center (under-construction-button :inbox-button (term/inbox))
             :east (under-construction-button :filter-button (term/filter))
             :hgap 3)
    :south (seesaw-core/scrollable
             (seesaw-core/listbox :id :recent-shares-listbox
                 ; :model A ListModel, or a sequence of values with which a DefaultListModel will be constructed.
                 ; :renderer A cell renderer to use. See (seesaw.cells/to-cell-renderer).
             )
             :preferred-size [panel-width :by 200])
    :vgap 3))

(defn create-online-friends []
  (seesaw-core/border-panel
    :north (seesaw-core/border-panel
             :west (seesaw-core/label :text (term/online-friends)
                                      :foreground title-color :font title-font)
             :east (under-construction-button :filter-online-friends-button (term/filter))
             :hgap 3)
    :south (seesaw-core/scrollable
             (seesaw-core/listbox :id :online-friends-listbox
                 ; :model A ListModel, or a sequence of values with which a DefaultListModel will be constructed.
                 ; :renderer A cell renderer to use. See (seesaw.cells/to-cell-renderer).
             )
             :preferred-size [panel-width :by 200])
    :vgap 3))

(defn create-main-panel
  "Creates the main status panel."
  []
  (seesaw-core/border-panel
    :id status-panel-id
    :north (create-update-status)
    :center (create-recent-shares)
    :south (create-online-friends)

    :vgap 15
    :border (seesaw-border/compound-border
              (seesaw-border/empty-border :thickness 10)
              (seesaw-border/line-border :right 1))))

(defn find-status-panel
  "Finds the status panel in the given view."
  [view]
  (view-utils/find-component view status-panel-id))

(defn find-update-status-button
  "Finds the update status button in the given view."
  [view]
  (view-utils/find-component view update-status-button-id))

(defn find-status-text
  "Finds the status text in the given view."
  [view]
  (view-utils/find-component view status-text-id))

(defn status-text
  ([view]
    (seesaw-core/text (find-status-text view)))
  ([view text]
    (seesaw-core/config! (find-status-text view) :text text)))

(defn add-action-listener-to-update-status-button
  "Adds the given action listener to the update status button."
  [view listener]
  (view-utils/add-action-listener-to-button
    (find-update-status-button view) listener
    update-status-button-listener-key))

(defn update-status-listener
  "Sends the text in the status text area as a status update."
  [event]
  (let [status-panel (find-status-panel
                       (view-utils/top-level-ancestor
                         (seesaw-core/to-widget event)))
        status-text-value (status-text status-panel)]
    (status-text status-panel "")
    (when (not-empty status-text-value)
      (future
        (send-status-call/send-to-default-group status-text-value)
        (logging/debug "Sent status update:" status-text-value)))))

(defn attach-update-status-listener
  "Attaches the update status listener to the update status button in the given
view."
  [view]
  (add-action-listener-to-update-status-button view update-status-listener))

(defn attach-listeners
  "Attaches all listeners and models to the given status panel."
  [view]
  (attach-update-status-listener view)
  view)

(defn create
  "Creates the status panel and attaches all listeners."
  []
  (attach-listeners (create-main-panel)))