(ns masques.database.migrations.20130330113200-drop-identity-table
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Drops the identity table."
  []
  (drop-table :identity))
  
(defn down
  "Recreates the identity table."
  []
  (create-table :identity
    (id)
    (string :name)
    (string :public_key)
    (string :public_key_algorithm)
    (belongs-to :peer)
    (integer :is_online)))
