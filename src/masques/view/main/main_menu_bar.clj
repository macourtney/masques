(ns masques.view.main.main-menu-bar
  (:require [clj-internationalization.term :as term]
            [seesaw.core :as seesaw-core]))

(defn exit []
  (seesaw-core/menu-item :id :exit-menu-item :text (term/exit)))

(defn file []
  (seesaw-core/menu :text (term/file) :items [(exit)]))

(defn create []
  (seesaw-core/menubar :items [(file)]))