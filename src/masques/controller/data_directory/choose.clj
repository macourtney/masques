(ns masques.controller.data-directory.choose
  (:require [clojure.tools.logging :as logging]
            [masques.controller.utils :as controller-utils]
            [masques.view.data-directory.choose :as choose-view]))

(defn load-data [parent-component]
  parent-component)

(defn attach [parent-component]
  parent-component)

(defn show []
  (controller-utils/show (attach (load-data (choose-view/create)))))