(ns masques.database.migrations.20130224211500-create-share
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the share table in the database."
  []
  (create-table :share
    (id)
    (timestamp :created_at)
    (uuid :uuid)
    (integer :message_id)
    (integer :content_id)
    (integer :group_id)
    (string :content_type)
    (text :identity_to)
    (string :identity_to_algorithm)
    (text :identity_from)
    (string :identity_from_algorithm)
    (timestamp :shown_in_stream_at)
    (timestamp :transferred_at)))
  
(defn down
  "Drops the share table in the database."
  []
  (drop-table :share))
