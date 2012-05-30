(ns masques.database.migrations.20120530141950-create-names
  (:use drift-db.core))

(defn up
  "Creates the names table."
  []
  (create-table :names
    (id)
    (string :name)
    (belongs-to :identity)))
  
(defn down
  "Deletes the names table."
  []
  (drop-table :names))