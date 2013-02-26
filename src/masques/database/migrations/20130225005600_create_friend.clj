(ns masques.database.migrations.20130225005600_create_friend
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the friend table in the database."
  []
  (create-table :friend
    (id)
    (timestamp :created_at)
    (string :request_status)
    (integer :profile_id)
    (timestamp :friend_requested_at)
    (timestamp :friend_request_approved_at)))
  
(defn down
  "Drops the friend table in the database."
  []
  (drop-table :friend))
