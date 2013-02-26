(ns masques.database.migrations.20130224221600_create_profile
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the profile table in the database."
  []
  (create-table :profile
    (id)
    (timestamp :created_at)
    (string :alias)
    (string :alias_nick)
    (string :time_zone)
    (integer :avatar_file_id)
    (integer :avatar_nick_file_id)
    (text :comments)
    (text :identity)
    (string :identity_algorithm)
    (text :private_key)
    (string :private_key_algorithm)
  
(defn down
  "Drops the profile table in the database."
  []
  (drop-table :profile))
