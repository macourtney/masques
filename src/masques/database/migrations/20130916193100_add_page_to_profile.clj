(ns masques.database.migrations.20130916193100-add-page-to-profile
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Adds the page field to the profile table."
  []
  (add-column :profile (text :page)))

(defn down
  "Removes the page field from the profile table."
  []
  (drop-column :profile :page))