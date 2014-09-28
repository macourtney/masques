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
    (string :identity { :length 512 })
    (string :identity-algorithm { :length 40 })
    (text :private-key)
    (string :private-key-algorithm { :length 40 })
    (text :destination)
    (text :page))
  
  (create-index :profile :profile-identity
                { :columns [:identity :identity-algorithm] :unique? true }))

(defn down
  "Drops the profile table."
  []
  (drop-index :profile-identity)
  
  (drop-table :profile))