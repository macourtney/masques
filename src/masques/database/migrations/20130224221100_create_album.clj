(ns masques.database.migrations.20130224221100-create-album
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Creates the album table in the database."
  []
  (create-table :album
    (id)
    (date-time :created-at)
    (string :name)
    (integer :size-of-all-files)
    (text :comments)))
  
(defn down
  "Drops the album table in the database."
  []
  (drop-table :album))
