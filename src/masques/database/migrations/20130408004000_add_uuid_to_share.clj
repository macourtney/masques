(ns masques.database.migrations.20130408004000-add-uuid-to-share
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Adds the user generated column to the groups table."
  []
  (add-column :share (string :uuid)))

(defn down
  "Removes the user generated column from the groups table."
  []
  (drop-column :share :uuid))