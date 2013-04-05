(ns masques.database.migrations.20120731153908-create-group-memberships
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Creates the group-memberships table."
  []
  (create-table :group-memberships
    (id)
    (belongs-to :group)
    (belongs-to :friend)))

(defn down
  "Drops the group-memberships table."
  []
  (drop-table :group-memberships))