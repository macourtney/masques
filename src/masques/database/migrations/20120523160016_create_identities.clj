(ns masques.database.migrations.20120523160016-create-identities
  (:use drift-db.core))

(defn up
  "Creates the identities table in the database."
  []
  (create-table :identities
    (id)
    (string :name)
    (string :public_key)
    (string :public_key_algorithm)
    (belongs-to :peer)))
  
(defn down
  "Drops the identities table in the database."
  []
  (drop-table :identities))