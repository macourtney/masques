(ns masques.database.migrations.20130224220100-create-file
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the file table in the database."
  []
  (create-table :file
    (id)
    (date-time :created_at)
    (string :name)
    (string :path)
    (integer :album_id)
    (string :mime_type)
    (text :comments)
    (integer :size)))
  
(defn down
  "Drops the file table in the database."
  []
  (drop-table :file))
