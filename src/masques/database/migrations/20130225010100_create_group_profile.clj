(ns masques.database.migrations.20130225010100_create_group_profile
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the group_profile table in the database."
  []
  (create-table :group_profile
    (id)
    (timestamp :added_at)
    (int :profile_id)
    (int :group_id)))
  
(defn down
  "Drops the group_profile table in the database."
  []
  (drop-table :group_profile))
