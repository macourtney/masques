(ns masques.database.migrations.20130329222700-change-group-profile-to-grouping-profile
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Change the name of the table, and the name of the related field."
  []
  (drop-table :grouping_profile)
  (drop-table :group_profile)
  (create-table :grouping_profile
    (id)
    (date-time :added_at)
    (belongs-to :grouping)
    (belongs-to :profile)))
  
(defn down
  "Drops the group_profile table in the database."
  []
  (drop-table :grouping_profile)
  (drop-table :group_profile)
  (create-table :group_profile
    (id)
    (date-time :added_at)
    (belongs-to :group)
    (belongs-to :profile)))
