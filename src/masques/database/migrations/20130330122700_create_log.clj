(ns masques.database.migrations.20130330122700-create-log
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Creates the log table in the database."
  []
  (create-table :log
    (id)
    (date-time :created_at)
    (string :action)
    (string :name_space)
    (text :meta_data)
    (text :message)))
  
(defn down
  "Drops the log table from the database."
  []
  (drop-table :log))
