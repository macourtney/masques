(ns masques.database.migrations.20130224215000-create-message
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the message table in the database."
  []
  (create-table :message
    (id)
    (timestamp :created_at)
    (string :subject)
    (text :body)))
  
(defn down
  "Drops the message table in the database."
  []
  (drop-table :message))
