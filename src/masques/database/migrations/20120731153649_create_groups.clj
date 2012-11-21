(ns masques.database.migrations.20120731153649-create-groups
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the groups table."
  []
  (create-table :groups
    (id)
    (belongs-to :identity)
    (string :name)))

(defn down
  "Drops the groups table."
  []
  (drop-table :groups))