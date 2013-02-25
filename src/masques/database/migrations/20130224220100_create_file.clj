(ns masques.database.migrations.20130224220100-create-file
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the file table in the database."
  []
  (create-table :file
    (id)
    (timestamp :created_at)
    (string :name)
    (string :path)
    (int :album_id)
    (string :mime_type)
    (text :comments)
    (int :size)
  
(defn down
  "Drops the file table in the database."
  []
  (drop-table :file))
