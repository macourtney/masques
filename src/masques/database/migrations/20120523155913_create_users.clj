(ns masques.database.migrations.20120523155913-create-users
  (:use drift-db.core))

(defn up
  "Creates the users table in the database."
  []
  (create-table :users
    (id)
    (string :name)
    (string :encrypted_password)
    (string :salt)
    (string :encrypted_password_algorithm)
    (integer :encrypted_password_n)
    (text :public_key)
    (string :public_key_algorithm)
    (text :private_key)
    (string :private_key_algorithm)
    (string :private_key_encryption_algorithm)))
  
(defn down
  "Drops the users table in the database."
  []
  (drop-table :users))