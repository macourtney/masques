(ns masques.database.migrations.20130329192700-use-singular-naming-convention
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Uses the singular noun naming convention."
  []
  
  (drop-table :users)
  (drop-table :user)
  (create-table :user
    (id)
    (string :name)
    (string :encrypted_password)
    (string :salt)
    (string :encrypted_password_algorithm)
    (integer :encrypted_password_n)
    (belongs-to :profile))

  (drop-table :properties)
  (drop-table :property)
  (create-table :property
    (id)
    (string :name)
    (string :value))

  (drop-table :peers)
  (drop-table :peer)
  (create-table :peer
    (id)
    (text :destination)
    (date-time :created_at)
    (date-time :updated_at)
    (integer :notified)
    (integer :local))
  
  (drop-table :identities)
  (drop-table :identity)
  (create-table :identity
    (id)
    (string :name)
    (string :public_key)
    (string :public_key_algorithm)
    (belongs-to :peer)
    (integer :is_online))

  (drop-table :groups)
  (drop-table :grouping)
  (create-table :grouping
    (id)
    (belongs-to :identity)
    (integer :user_generated { :length 1 })
    (string :name)))
  
(defn down
  "Uses the plural noun naming convention."
  []
  
  (drop-table :account)
  (drop-table :user)
  (create-table :users
    (id)
    (string :name)
    (string :encrypted_password)
    (string :salt)
    (string :encrypted_password_algorithm)
    (integer :encrypted_password_n)
    (belongs-to :profile))

  (drop-table :property)
  (drop-table :properties)
  (create-table :properties
    (id)
    (string :name)
    (string :value))

  (drop-table :peers)
  (drop-table :peer)
  (create-table :peers
    (id)
    (text :destination)
    (date-time :created_at)
    (date-time :updated_at)
    (integer :notified)
    (integer :local))

  (drop-table :identities)
  (drop-table :identity)
  (create-table :identities
    (id)
    (string :name)
    (string :public_key)
    (string :public_key_algorithm)
    (belongs-to :peer)
    (integer :is_online))

  (drop-table :groups)
  (drop-table :grouping)
  (create-table :groups
    (id)
    (belongs-to :identity)
    (integer :user_generated { :length 1 })
    (string :name)))
