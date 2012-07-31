(ns masques.database.migrations.20120731153649-create-groups
  (:use drift-db.core))

(defn up
  "Creates the groups table."
  []
  (create-table :groups
    (id)
    (string :name)))

(defn down
  "Drops the groups table."
  []
  (drop-table :groups))