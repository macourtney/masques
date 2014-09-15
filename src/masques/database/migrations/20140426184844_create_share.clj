(ns masques.database.migrations.20140426184844-create-share
  (:use drift-db.core))

(defn up
  "Creates the share table in the database."
  []
  (create-table :share
    (id)
    (string :uuid)
    (date-time :created-at)
    (integer :message-id)
    (integer :content-id)
    (string :content-type)
    (integer :profile-from-id))
  
  (create-index :share :share-uuid { :columns [:uuid] }))

(defn down
  "Drops the share table in the database."
  []
  (drop-index :share-uuid)
  
  (drop-table :share))