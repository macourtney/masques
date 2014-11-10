(ns masques.view.stream.panel
  (:require [clj-internationalization.term :as term]
            [masques.view.stream.stream-list-model :as stream-list-model]
            [masques.view.utils :as view-utils]
            [masques.view.utils.korma-list-model :as korma-list-model]
            [seesaw.border :as seesaw-border]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core])
  (:import [java.awt Color]))

(def field-size [150 :by 25])
(def button-size [100 :by 25])
(def date-range-text-size [50 :by 25])

(def share-types [:friend-request :file])

(def group-button-font { :name "DIALOG" :style :plain :size 10 })

(def stream-listbox-id :stream-listbox)

(defn create-under-construction-button
  ([id text]
    (view-utils/create-under-construction-link-button
      :id id
      :text text
      :font group-button-font))
  ([id text size]
    (let [button (create-under-construction-button id text)]
      (seesaw-core/config! button :preferred-size size :maximum-size size)
      button)))

(defn create-group-panel []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/group)
            (seesaw-core/combobox :id :group-combobox :preferred-size field-size
                                  :maximum-size field-size)
            (create-under-construction-button
              :hide-search-box (term/hide-search-box) button-size)]))

(defn create-share-type-panel []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/share-type)
            (seesaw-core/combobox
              :id :share-type-combobox :preferred-size field-size
              :maximum-size field-size)
            (create-under-construction-button
              :clear-search (term/clear-search) button-size)]))

(defn create-friend-panel []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/friend)
            (seesaw-core/text :id :friend-text :preferred-size field-size
                              :maximum-size field-size)
            (seesaw-core/flow-panel :items [] :preferred-size button-size
                                    :maximum-size button-size)]))

(defn create-date-range-panel []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/date-range)
            (seesaw-core/flow-panel
              :items [(seesaw-core/text
                        :id :begin-date-text
                        :preferred-size date-range-text-size
                        :maximum-size date-range-text-size)
                      (term/to)
                      (seesaw-core/text
                        :id :end-date-text :preferred-size date-range-text-size
                        :maximum-size date-range-text-size)]
              :preferred-size field-size
              :maximum-size field-size
              :border 0)
            (seesaw-core/flow-panel
              :items [] :preferred-size button-size :maximum-size button-size)]
    :border 0))

(defn create-search-terms-panel []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/search-terms)
            (seesaw-core/text :id :search-terms-text :preferred-size field-size
                              :maximum-size field-size)
            (create-under-construction-button
              :search-stream (term/search-stream) button-size)]))

(defn create-search []
  (seesaw-core/vertical-panel
      :items [(create-group-panel) [:fill-v 3] (create-share-type-panel)
              [:fill-v 3] (create-friend-panel) [:fill-v 3]
              (create-date-range-panel) [:fill-v 3] (create-search-terms-panel)]

      :border (seesaw-border/line-border :thickness 1 :color (Color/GRAY))))

(defn create-header []
  (seesaw-core/border-panel
    :west (create-search)
    :east (seesaw-core/vertical-panel
            :items [(seesaw-core/label :text (term/stream) :foreground "#380B61"
                                       :font { :size 48 })])))

(defn create-stream-buttons []
  (seesaw-core/horizontal-panel
    :items [(create-under-construction-button :filter-all-shares
                                              (term/all-shares))
            " | " (create-under-construction-button :inbox (term/inbox))
            " | " (create-under-construction-button :from-me (term/from-me))]))

(defn create-stream-list []
  (seesaw-core/scrollable
     (seesaw-core/listbox
       :id stream-listbox-id
       :model (stream-list-model/create)
         ; :renderer A cell renderer to use. See
         ; (seesaw.cells/to-cell-renderer).
     )))

(defn create-body []
  (seesaw-core/border-panel
    :north (create-stream-buttons)
    :center (create-stream-list)
    
    :vgap 5))

(defn create []
  (seesaw-core/border-panel
    :north (create-header)
    :center (create-body)
    
    :vgap 10
    :border 11))

(defn find-stream-list
  "Returns the stream list from the given panel view."
  [view]
  (view-utils/find-component view stream-listbox-id))

(defn destroy
  "Cleans up the given stream panel."
  [view]
  (korma-list-model/destroy-model (find-stream-list view)))