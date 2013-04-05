(ns masques.database.migrations.20130225005900-create-group
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Creates the group table in the database."
  []
  ;(create-table :group
  ;  (id)
  ;  (date-time :created_at)
  ;  (string :name))
  )
  
(defn down
  "Drops the group table in the database."
  []
  ;(drop-table :group)
  )
