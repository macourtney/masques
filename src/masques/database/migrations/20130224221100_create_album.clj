(ns masques.database.migrations.20130224221100_create_album
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the album table in the database."
  []
  (create-table :album
    (id)
    (timestamp :created_at)
    (string :name)
    (integer :size_of_all_files)
    (text :comments)))
  
(defn down
  "Drops the album table in the database."
  []
  (drop-table :album))
