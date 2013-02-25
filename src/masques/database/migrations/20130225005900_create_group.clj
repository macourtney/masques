(ns masques.database.migrations.20130225005900_create_group
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the group table in the database."
  []
  (create-table :group
    (id)
    (timestamp :created_at)
    (string :name)))
  
(defn down
  "Drops the group table in the database."
  []
  (drop-table :group))
