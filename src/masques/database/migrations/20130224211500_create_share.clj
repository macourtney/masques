(ns masques.database.migrations.20120523155913-create-users
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the share table in the database."
  []
  (create-table :share
    (id)
    (string :uuid)
    (int :message_id)
    (int :content_id)
    (int :group_id)
    (string :content_type)
    (text :identity_to)
    (string :identity_to_algorithm)
    (text :identity_from)
    (string :identity_from_algorithm)
    (timestamp :created_at)
    (timestamp :shown_in_stream_at)
    (timestamp :transferred_at)))
  
(defn down
  "Drops the share table in the database."
  []
  (drop-table :share))
