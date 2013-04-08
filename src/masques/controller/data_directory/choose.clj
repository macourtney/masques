(ns masques.controller.data-directory.choose
  (:require [clojure.tools.logging :as logging]
            [config.db-config :as db-config]
            [masques.controller.utils :as controller-utils]
            [masques.view.data-directory.choose :as choose-view]
            [seesaw.core :as seesaw-core]))

(defn update-data-directory [parent-component path]
  (.setText (choose-view/data-directory-text parent-component) path))

(defn load-data [parent-component]
  (update-data-directory parent-component (db-config/data-dir))
  parent-component)

(defn create-choose-directory-action [parent-component]
  (fn [e]
    (update-data-directory
      parent-component
      (.getPath (controller-utils/choose-file parent-component)))))

(defn attach [parent-component]
  (seesaw-core/listen (choose-view/choose-button parent-component)
          :action (create-choose-directory-action parent-component))
  parent-component)

(defn show []
  (controller-utils/show (attach (load-data (choose-view/create)))))