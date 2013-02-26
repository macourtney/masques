(ns masques.database.migrations.201302252000_create_comment
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the comment table in the database."
  []
  (create-table :comment
    (id)
    (timestamp :created_at)
    (integer :share_id)
    (text :body)))
  
(defn down
  "Drops the comment table in the database."
  []
  (drop-table :comment))
