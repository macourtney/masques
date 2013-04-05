(ns masques.database.migrations.20120523160016-create-identities
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Creates the identities table in the database."
  []
  (create-table :identities
    (id)
    (string :name)
    (string :public_key)
    (string :public_key_algorithm)
    (belongs-to :peer)
    (integer :is_online)))
  
(defn down
  "Drops the identities table in the database."
  []
  (drop-table :identities))