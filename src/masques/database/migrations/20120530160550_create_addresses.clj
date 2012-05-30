(ns masques.database.migrations.20120530160550-create-addresses
  (:use drift-db.core))

(defn up
  "Creates the addresses table."
  []
  (create-table :addresses
    (id)
    (string :address)
    (belongs-to :identity)))
  
(defn down
  "Deletes the addresses table."
  []
  (drop-table :addresses))