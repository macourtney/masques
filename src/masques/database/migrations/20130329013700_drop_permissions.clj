(ns masques.database.migrations.20130329013700-drop-permissions
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Drops the permissions table."
  []
  (drop-table :permissions))

(defn down
  "Creates the permissions table."
  []
  (create-table :permissions
    (id)
    (belongs-to :identity)
    (string :name)))
