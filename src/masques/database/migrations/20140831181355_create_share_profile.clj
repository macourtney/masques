(ns masques.database.migrations.20140831181355-create-share-profile
  (:use drift-db.core))

(defn up
  "Creates the share profile table in the database."
  []
  (create-table :share-profile
    (id)
    (integer :share-id)
    (integer :group-id)
    (integer :profile-to-id)
    (date-time :shown-in-stream-at)
    (date-time :transferred-at))
  
  (create-index :share-profile :share-profile-share-id { :columns [:share-id] })
  (create-index :share-profile :share-profile-share-id-profile-to-id
                { :columns [:share-id :profile-to-id] :unique? true }))

(defn down
  "Drops the share profile table in the database."
  []
  (drop-index :share-profile-share-id)
  (drop-index :share-profile-share-id-profile-to-id)
  
  (drop-table :share-profile))