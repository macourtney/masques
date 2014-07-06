(ns masques.view.main.display-panel
  (:require [clojure.tools.logging :as logging]
            [masques.controller.main.panel-protocol :as panel-protocol]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(def id "display-panel")
(def display-card-panel-id "display-card-panel")
(def panel-map-id :panel-map)
(def displayed-panel-id :displayed-panel)
(def panel-id :panel)
(def view-id :view)

(defn create-card-panel []
  (seesaw-core/card-panel :id display-card-panel-id :items []))

(defn create []
  (seesaw-core/border-panel
    :id id
    :center (create-card-panel)))

(defn find-card-panel
  "Returns the card panel containing all of the views for the panels in this display."
  [display-panel]
  (view-utils/find-component display-panel (str "#" display-card-panel-id)))

(defn find-panel-map
  "Returns the panel map attached to the given display panel or an empty map."
  [display-panel]
  (or (view-utils/retrieve-component-property
        (find-card-panel display-panel) panel-map-id)
      {}))

(defn find-panel-view
  "Returns the view for the given panel (or panel id)."
  [display-panel panel]
  (get (get (find-panel-map display-panel) (panel-protocol/find-panel-name panel)) :view))

(defn find-panel
  "Returns the panel for the given panel id. If panel-id is not a string or keyword then this function simply returns panel-id."
  [display-panel panel-id]
  (if (or (string? panel-id) (keyword? panel-id)) 
    (get (get (find-panel-map display-panel) panel-id) :panel)
    panel-id))

(defn save-panel-view
  "Saves the given panel and panel view to the panel map for retrieval later."
  [display-panel panel panel-view]
  (view-utils/save-component-property
    (find-card-panel display-panel)
    panel-map-id
    (assoc (find-panel-map display-panel) (panel-protocol/panel-name panel)
           { panel-id panel view-id panel-view })))

(defn find-displayed-panel
  "Returns the id of the currently displayed panel."
  [display-panel]
  (view-utils/retrieve-component-property (find-card-panel display-panel) displayed-panel-id))

(defn save-displayed-panel
  "Saves the id of the given panel as the currently displayed panel."
  [display-panel panel]
  (view-utils/save-component-property (find-card-panel display-panel) displayed-panel-id
                                      (panel-protocol/find-panel-name panel)))

(defn panel-as-vector
  "Converts a panel and panel view into a vector for use an item of the card panel."
  [panel panel-view]
  [panel-view (name (panel-protocol/panel-name panel))])

(defn panel-map-as-vector
  "Converts a map for a single panel into a vector for use an item of the card panel."
  [panel-map]
  (panel-as-vector (:panel panel-map) (:view panel-map)))

(defn find-panels-as-items
  "Returns all of the panels in a list for use in the items list of the card panel."
  [display-panel]
  (map panel-map-as-vector (vals (find-panel-map display-panel))))

(defn add-panel
  "Adds the given panel to the view."
  [display-panel panel show-panel-fn]
  (let [card-panel (find-card-panel display-panel)
        panel-view (panel-protocol/create-view panel)]
    (seesaw-core/config! card-panel
                         :items (cons (panel-as-vector panel panel-view)
                                      (find-panels-as-items display-panel)))
    (save-panel-view display-panel panel panel-view)
    (panel-protocol/init panel panel-view show-panel-fn)))

(defn update-displayed-panel
  "Updates the displayed panel to the given panel."
  [display-panel panel]
  (let [previous-panel-id (find-displayed-panel display-panel)]
    (save-displayed-panel display-panel panel)
    (when (and previous-panel-id (not (= previous-panel-id (panel-protocol/find-panel-name panel))))
      (panel-protocol/hide (find-panel display-panel previous-panel-id)
                         (find-panel-view display-panel previous-panel-id)))))

(defn show-panel
  "Shows the given panel (or panel id)."
  [display-panel panel args]
  (when panel
    (let [card-panel (find-card-panel display-panel)
          panel-view (find-panel-view display-panel panel)
          panel (find-panel display-panel panel)]
      (panel-protocol/show panel panel-view args)
      (.show (.getLayout card-panel) card-panel (name (panel-protocol/find-panel-name panel)))
      (update-displayed-panel display-panel panel))))

(defn destroy-all-panels
  "Calls destroy for all panels in the display panel."
  [display-panel]
  (doseq [panel-map (vals (find-panel-map display-panel))]
    (when panel-map
      (when-let [panel (panel-id panel-map)]
        (panel-protocol/destroy panel (view-id panel-map))))))