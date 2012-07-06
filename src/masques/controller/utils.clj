(ns masques.controller.utils
  (:require [clojure.tools.logging :as logging]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table]
            [seesaw.widget-options :as widget-options])
  (:import [java.awt Color]
           [java.awt.event ItemListener]
           [javax.swing JComponent JFileChooser JLayeredPane JRootPane]))

(def yellow-highlight (Color. (float 1) (float 1) (float 0.6)))
(def turquoise-highlight (Color. (float 0.7) (float 1) (float 1)))
(def gray-highlight (Color. (float 0.92) (float 0.92) (float 0.92)))

(defn find-component [parent-component id]
  (seesaw-core/select parent-component [id]))

(defn show [frame]
  (if frame
    (seesaw-core/show! frame)
    (logging/warn "Show was given a nil frame.")))

(defn create-item-listener [item-listener-fn]
  (reify ItemListener
    (itemStateChanged [this e]
      (item-listener-fn e))))

(defn attach-item-listener [component item-listener-fn]
  (.addItemListener component (create-item-listener item-listener-fn))
  component)

(defn table-row-pairs [table]
  (map #(list %1 (seesaw-table/value-at table %1))
    (range (seesaw-table/row-count table))))

(defn find-table-record-pair [table record-id]
  (some #(when (= (:id (second %1)) record-id) %1)
    (table-row-pairs table)))

(defn delete-record-from-table [table record-id]
  (when-let [record-index (first (find-table-record-pair table record-id))]
    (seesaw-table/remove-at! table record-index)
    record-index))

(defn add-record-to-table 
  ([table record] (add-record-to-table table record 0))
  ([table record index]
    (seesaw-table/insert-at! table (or index 0) record)))

(defn update-record-in-table [table record]
  (add-record-to-table table record (delete-record-from-table table (:id record))))

(defn enableable-widget? [component]
  (satisfies? widget-options/WidgetOptionProvider component))

(defn enableable-widgets [parent-component]
  (when parent-component
    (filter enableable-widget? (seesaw-core/select parent-component [:*]))))

(defn enable-subwidgets [parent-component enable?]
  (seesaw-core/config! (enableable-widgets parent-component) :enabled? enable?))

(defn disable-widget [parent-component]
  (enable-subwidgets parent-component false))

(defn enable-widget [parent-component]
  (enable-subwidgets parent-component true))

(defn save-component-property [component key value]
  (.putClientProperty component key value)
  value)

(defn retrieve-component-property [component key]
  (.getClientProperty component key))

(defn remove-component-property [component key]
  (let [value (retrieve-component-property component key)]
    (save-component-property component key nil)
    value))

(defn attach-and-save-listener [component add-listener-fn key listener]
  (add-listener-fn (save-component-property component key listener))
  component)

(defn detach-and-remove-listener [component remove-listener-fn key]
  (remove-listener-fn (retrieve-component-property component key))
  component)

(defn attach-and-detach-listener [window listener key component-find-fn add-listener-fn remove-listener-fn]
  (let [component (component-find-fn window)]
    (seesaw-core/listen window
                      :window-opened (fn [e] (attach-and-save-listener component add-listener-fn key listener))
                      :window-closed (fn [e] (detach-and-remove-listener component remove-listener-fn key))))
  window)

(defn choose-file
  "Pops up a file chooser and returns the chosen file if the user picks one, otherwise this function returns nil."
  [owner]
  (let [file-chooser (new JFileChooser)]
    (when (= JFileChooser/APPROVE_OPTION (.showOpenDialog file-chooser owner))
      (.getSelectedFile file-chooser))))