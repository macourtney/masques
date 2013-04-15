(ns masques.database.migrations.20130329013900-drop-group-memberships
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Drops the group-memberships table."
  []
  (drop-table :group-memberships))

(defn down
  "Creates the group-memberships table."
  []
  (create-table :group-memberships
    (id)
    (belongs-to :group)
    (belongs-to :friend)))

