(ns masques.database.migrations.20120806155239-drop-permissions-identity-column
  (:use drift-db.core))

(defn up
  "Drops the identity id column from the permissions table."
  []
  (drop-column :permissions :identity_id))
  
(defn down
  "Adds the identity id column to the permissions table."
  []
  (add-column :permissions (belongs-to :identity)))