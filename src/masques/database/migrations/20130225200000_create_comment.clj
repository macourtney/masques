(ns masques.database.migrations.20130225200000-create-comment
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Creates the comment table in the database."
  []
  (create-table :comment
    (id)
    (date-time :created_at)
    (integer :share_id)
    (integer :comment_id) ;"Optional"
    (text :body)))
  
(defn down
  "Drops the comment table in the database."
  []
  (drop-table :comment))
