(ns masques.database.migrations.20120601135705-create-friends
  (:use drift-db.core))

(defn up
  "Migrates the database up to version 20120601135705."
  []
  (create-table :friends
    (id)
    (belongs-to :identity)
    (belongs-to :friend)))
  
(defn down
  "Migrates the database down from version 20120601135705."
  []
  (drop-table :friends))