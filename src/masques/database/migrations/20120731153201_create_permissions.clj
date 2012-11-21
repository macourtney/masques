(ns masques.database.migrations.20120731153201-create-permissions
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the permissions table."
  []
  (create-table :permissions
    (id)
    (belongs-to :identity)
    (string :name)))

(defn down
  "Drops the permissions table."
  []
  (drop-table :permissions))