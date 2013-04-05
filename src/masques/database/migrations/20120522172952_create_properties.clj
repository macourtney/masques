(ns masques.database.migrations.20120522172952-create-properties
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Creates the properties table."
  []
  (create-table :properties
    (id)
    (string :name)
    (string :value)))
  
(defn down
  "Drops the properties table."
  []
  (drop-table :properties))