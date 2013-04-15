(ns masques.database.migrations.20130329014100-drop-group-permissions
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Drops the group-memberships table."
  []
  (drop-table :group-permissions))

(defn down
  "Creates the group-permissions table."
  []
  (create-table :group-permissions
    (id)
    (belongs-to :group)
    (belongs-to :permission)
    (string :type { :length 10 })))
