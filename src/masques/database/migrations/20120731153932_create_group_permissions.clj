(ns masques.database.migrations.20120731153932-create-group-permissions
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the group-permissions table."
  []
  (create-table :group-permissions
    (id)
    (belongs-to :group)
    (belongs-to :permission)
    (string :type { :length 10 })))

(defn down
  "Drops the group-memberships table."
  []
  (drop-table :group-permissions))