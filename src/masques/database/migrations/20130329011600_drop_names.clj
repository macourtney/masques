(ns masques.database.migrations.20130329011600-drop-names
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn down
  "Creates the names table."
  []
  (create-table :names
    (id)
    (string :name)
    (belongs-to :identity)))
  
(defn up
  "Deletes the names table."
  []
  (drop-table :names))