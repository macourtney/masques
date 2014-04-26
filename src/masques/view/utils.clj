(ns masques.view.utils
  (:require [seesaw.chooser :as seesaw-chooser]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing ImageIcon JFileChooser UIManager]))

(def link-color "#FFAA00")

(def link-button-font { :name "DIALOG" :style :plain :size 12 })

(def under-construction-image
  (ImageIcon.
    (ClassLoader/getSystemResource "Road-under-construction-tiny.png")))

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
  (when parent-component
    (let [select-vector (if (vector? id) id [(to-select-id id)])]
      (seesaw-core/select parent-component select-vector))))

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
      :success-fn success-fn)))

()

(defn add-mouse-over-background-change
  "Adds mouse listeners to change the background of a widget when the mouse is
over it. Options:

  :widget - The widget to add the background change to.
  :background - The original background of the given widget. If nil then uses
                the UIManager. Default nil.
  :hover-color - The background color when the mouse is over the widget. Default
                 :lightgray.
  :pressed-color - The background color when the moused is pressed on the
                   widget. Default :white."
  [& args]
    (let [{ :keys [widget background hover-color pressed-color]
            :or { background nil hover-color :lightgray pressed-color :white } }
            (apply hash-map args)]
      (seesaw-core/listen widget
        :mouse-entered
          (fn [event]
            (seesaw-core/config! (seesaw-core/to-widget event)
                                 :background hover-color))

        :mouse-exited
          (fn [event]
            (seesaw-core/config! (seesaw-core/to-widget event)
                                 :background (or background
                                                 (UIManager/getColor "control"))))
      
        :mouse-pressed
          (fn [event]
            (seesaw-core/config! (seesaw-core/to-widget event)
                                 :background pressed-color))
    
        :mouse-released
          (fn [event]
            (seesaw-core/config! (seesaw-core/to-widget event)
                                 :background hover-color)))))

(defn create-link-button
  "Creates a borderless button which looks some what like a webpage link."
  [& args]
  (let [opts (apply hash-map args)
        link-button (apply seesaw-core/button
                           (mapcat identity
                             (merge { :font link-button-font :border 5 }
                               (dissoc opts :hover-color :pressed-color))))]
    (apply add-mouse-over-background-change
      :widget link-button
      (mapcat identity
              (select-keys opts [:background :hover-color :pressed-color])))
    link-button))

(defn create-under-construction-link-button
  "Creates a borderless button which looks some what like a webpage link, but
also includes an image icon of a construction worker indicating this button has
not yet been implemented."
  [& args]
  (apply create-link-button :icon under-construction-image args))

(defn add-action-listener-to-button
  "Adds the given listener to the given button saving the listener-remover to
the button as a property with the given listener key. If no listener-key is
given, then :listener-remover is used."
  ([button listener] (add-action-listener-to-button
                       button listener :listener-remover))
  ([button listener listener-key]
    (when (and button listener)
      (let [listener-remover (seesaw-core/listen
                               button :action-performed listener)]
        (save-component-property button listener-key listener-remover)))))