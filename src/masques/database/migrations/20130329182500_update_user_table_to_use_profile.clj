(ns masques.database.migrations.20130329182500-update-user-table-to-use-profile
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Uses profile table, to store keys and related data."
  []
  (drop-column :users :public_key)
  (drop-column :users :public_key_algorithm)
  (drop-column :users :private_key)
  (drop-column :users :private_key_algorithm)
  (drop-column :users :private_key_encryption_algorithm)
  (add-column :users (belongs-to :profile)))
  
(defn down
  "Reverts back to storing keys and related data in the user table."
  []
  (add-column :users (text :public_key))
  (add-column :users (string :public_key_algorithm))
  (add-column :users (text :private_key))
  (add-column :users (string :private_key_algorithm))
  (add-column :users (string :private_key_encryption_algorithm))
  (drop-column :users :profile_id))
