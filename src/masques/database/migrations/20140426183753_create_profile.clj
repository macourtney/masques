(ns masques.database.migrations.20140426183753-create-profile
  (:use drift-db.core))

(defn up
  "Creates the profile table."
  []
  (create-table :profile
    (id)
    (date-time :created-at)
    (string :alias)
    (string :alias-nick)
    (string :time-zone)
    (integer :avatar-file-id)
    (integer :avatar-nick-file-id)
    (text :comments)
    (text :identity)
    (string :identity-algorithm)
    (text :private-key)
    (string :private-key-algorithm)
    (text :destination)
    (text :page)))

(defn down
  "Drops the profile table."
  []
  (drop-table :profile))