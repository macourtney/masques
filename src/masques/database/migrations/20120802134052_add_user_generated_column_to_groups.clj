(ns masques.database.migrations.20120802134052-add-user-generated-column-to-groups
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Adds the user generated column to the groups table."
  []
  (add-column :groups (integer :user_generated { :length 1 })))

(defn down
  "Removes the user generated column from the groups table."
  []
  (drop-column :groups :user_generated))