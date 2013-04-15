(ns masques.database.migrations.20130330151800-add-created-at-fields
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates consistent CREATED_AT fields."
  []
  (add-column :grouping (date-time :created_at))
  (add-column :grouping-profile (date-time :created_at))
  (add-column :user (date-time :created_at))
  (drop-column :grouping-profile :added_at))
  
(defn down
  "Goes back to the bad old days when we didn't have consistent created_at fields."
  []
  (drop-column :grouping :created_at)
  (drop-column :grouping-profile :created_at)
  (drop-column :user :created_at)
  (add-column :grouping-profile (date-time :added_at)))
