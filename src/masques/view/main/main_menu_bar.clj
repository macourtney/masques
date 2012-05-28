(ns masques.view.main.main-menu-bar
  (:require [clj-internationalization.core :as clj-i18n]
            [seesaw.core :as seesaw-core]))

(defn exit []
  (seesaw-core/menu-item :id :exit-menu-item :text (clj-i18n/exit)))

(defn file []
  (seesaw-core/menu :text (clj-i18n/file) :items [(exit)]))

(defn create []
  (seesaw-core/menubar :items [(file)]))