(ns masques.database.migrations.20130224220100-create-file
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Creates the file table in the database."
  []
  (create-table :file
    (id)
    (date-time :created-at)
    (string :name)
    (string :path)
    (integer :album-id)
    (string :mime-type)
    (string :comments)
    (integer :size)))
  
(defn down
  "Drops the file table in the database."
  []
  (drop-table :file))
