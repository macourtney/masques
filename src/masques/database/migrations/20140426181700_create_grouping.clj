(ns masques.database.migrations.20140426181700-create-grouping
  (:use drift-db.core))

(defn up
  "Creates the grouping table."
  []
  (create-table :grouping
    (id)
    (boolean :user-generated)
    (string :name)
    (string :display)
    (date-time :created-at)))

(defn down
  "Drops the grouping table."
  []
  (drop-table :grouping))