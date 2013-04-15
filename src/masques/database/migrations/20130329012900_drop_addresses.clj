(ns masques.database.migrations.20130329012900-drop-addresses
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Deletes the addresses table."
  []
  (drop-table :addresses))

(defn down
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
