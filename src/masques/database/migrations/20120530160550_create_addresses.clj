(ns masques.database.migrations.20120530160550-create-addresses
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Creates the addresses table."
  []
  (create-table :addresses
    (id)
    (string :address)
    (string :country)
    (string :province)
    (string :city)
    (string :postal_code)
    (belongs-to :identity)))
  
(defn down
  "Deletes the addresses table."
  []
  (drop-table :addresses))