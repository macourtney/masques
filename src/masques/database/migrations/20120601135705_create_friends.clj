(ns masques.database.migrations.20120601135705-create-friends
  (:use drift-db.core))

(defn up
  "Creates the friends table."
  []
  (create-table :friends
    (id)
    (belongs-to :identity)
    (belongs-to :friend)))

(defn down
  "Drops the friends table."
  []
  (drop-table :friends))