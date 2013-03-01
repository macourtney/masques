(ns masques.database.migrations.20130225005600-create-friend
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the friend table in the database."
  []
  (create-table :friend
    (id)
    (date-time :created_at)
    (string :request_status)
    (integer :profile_id)
    (date-time :friend_requested_at)
    (date-time :friend_request_approved_at)))
  
(defn down
  "Drops the friend table in the database."
  []
  (drop-table :friend))
