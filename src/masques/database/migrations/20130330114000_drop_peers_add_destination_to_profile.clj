(ns masques.database.migrations.20130330114000-drop-peers-add-destination-to-profile
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))
  
(defn up
  "Drops the peer table in the database."
  []
  (add-column :profile (text :destination))
  (drop-table :peer))

(defn down
  "Creates the peer table in the database."
  []
  (drop-column :profile :destination)
  (drop-table :peer)
  (create-table :peer
    (id)
    (text :destination)
    (date-time :created_at)
    (date-time :updated_at)
    (integer :notified)
    (integer :local)))
