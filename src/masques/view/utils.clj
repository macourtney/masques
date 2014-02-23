(ns masques.view.utils
  (:require [seesaw.chooser :as seesaw-chooser]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JFileChooser]))

(def link-color "#FFAA00")

(defn create-link-font [size]
  { :name "DIALOG" :style :bold :size size })

(defn center-window-on [parent window]
  (seesaw-core/pack! window)
  (.setLocationRelativeTo window parent)
  window)

(defn center-window [window]
  (center-window-on nil window))

(defn to-select-id
  "Converts the given id to a string for use in the select function of seesaw."
  [id]
  (let [id-str (if (keyword? id) (name id) id)]
    (if (.startsWith id-str "#")
      id-str
      (str "#" id-str))))

(defn find-component
  "Finds the given component with the given id which is a child component of the
given parent component."
  [parent-component id]
  (let []
    (seesaw-core/select parent-component [(to-select-id id)])))

(defn save-component-property [component key value]
  (.putClientProperty component key value)
  value)

(defn retrieve-component-property [component key]
  (.getClientProperty component key))

(defn remove-component-property [component key]
  (let [value (retrieve-component-property component key)]
    (save-component-property component key nil)
    value))
    
(defn top-level-ancestor
"Returns the top-level ancestor of the given component (either the containing
Window or Applet), or null if the component is null or has not been added to any
container."
  [component]
  (when component
    (.getTopLevelAncestor component)))

(defn choose-file
  "Pops up a file chooser and returns the chosen file if the user picks one,
otherwise this function returns nil."
  ([owner] (choose-file owner nil))
  ([owner file-selection-mode] (choose-file owner file-selection-mode nil))
  ([owner file-selection-mode dialog-type]
    (let [file-chooser (new JFileChooser)]
      (when file-selection-mode
        (.setFileSelectionMode file-chooser file-selection-mode))
      (when (= JFileChooser/APPROVE_OPTION
               (if (= JFileChooser/SAVE_DIALOG dialog-type)
                 (.showSaveDialog file-chooser owner)
                 (.showOpenDialog file-chooser owner)))
        (.getSelectedFile file-chooser)))))

(defn choose-directory
  "Pops up a file chooser which only chooses directories. Returns the chosen
directory or nil if the user does not select one."
  [owner]
  (choose-file owner JFileChooser/DIRECTORIES_ONLY))

(defn save-file
  "Pops up a file chooser for saving a file. Returns the chosen file or nil if
the user does not select one."
  ([owner success-fn]
    (save-file owner success-fn
               [(seesaw-chooser/file-filter "All files" (constantly true))]))
  ([owner success-fn filters] 
    (seesaw-chooser/choose-file
      :type :save
      :selection-mode :files-only
      :filters filters
      :success-fn success-fn))
  ;(choose-file owner JFileChooser/FILES_ONLY
  ;                     JFileChooser/SAVE_DIALOG)
  )