(ns masques.view.friend.utils
  (:require [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(def card-panel-id "friend-panel")
(def friend-panel-key :friend-panel)
(def show-panel-fn-key :show-panel-fn)

(def mid-file-filters [["Masques Id File" ["mid"]]
                       ["Folders" (fn [file] (.isDirectory file))]])

(defn find-friend-panel
  "Finds the export mid button in the given view."
  [view]
  (view-utils/find-component view card-panel-id))

(defn find-panel
  "Finds the panel attached to the view."
  [view]
  (view-utils/retrieve-component-property (find-friend-panel view)
                                          friend-panel-key))

(defn save-panel
  "Saves the given panel to the given view."
  [view panel]
  (view-utils/save-component-property view friend-panel-key panel))

(defn find-show-panel-fn
  "Finds the show panel fn attached to the view."
  [view]
  (view-utils/retrieve-component-property (find-friend-panel view)
                                          show-panel-fn-key))

(defn save-show-panel-fn
  "Saves the given show panel funciton to the given view."
  [view show-panel-fn]
  (view-utils/save-component-property (find-friend-panel view)
                                      show-panel-fn-key
                                      show-panel-fn))