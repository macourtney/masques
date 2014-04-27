(ns masques.database.migrations.20140426184844-create-share
  (:use drift-db.core))

(defn up
  "Creates the share table in the database."
  []
  (create-table :share
    (id)
    (date-time :created-at)
    (integer :message-id)
    (integer :content-id)
    (integer :group-id)
    (string :content-type)
    (date-time :shown-in-stream-at)
    (date-time :transferred-at)
    (string :uuid)
    (integer :profile-to-id)
    (integer :profile-from-id)))

(defn down
  "Drops the share table in the database."
  []
  (drop-table :share))