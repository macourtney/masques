(ns masques.database.migrations.20120522173019-create-peers
  (:use drift-db.core))

(defn up
  "Creates the peers table in the database."
  []
  (create-table :peers
    (id)
    (text :destination)
    (date-time :created_at)
    (date-time :updated_at)
    (integer :notified)
    (integer :local)))
  
(defn down
  "Drops the peers table in the database."
  []
  (drop-table :peers))