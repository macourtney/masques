(ns masques.database.migrations.20130225010100-create-group-profile
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the group_profile table in the database."
  []
  (create-table :group_profile
    (id)
    (date-time :added_at)
    (integer :profile_id)
    (integer :group_id)))
  
(defn down
  "Drops the group_profile table in the database."
  []
  (drop-table :group_profile))
