(ns masques.database.migrations.20140426180029-create-friend-request
  (:use drift-db.core))

(defn up
  "Migrates the database up to version 20140426180029."
  []
  (create-table :friend-request
    (id)
    (date-time :created-at)
    (string :request-status)
    (date-time :requested-at) ; Notice that this item and the one below it now have better names.
    (date-time :request-approved-at)
    (belongs-to :profile)))

(defn down
  "Migrates the database down from version 20140426180029."
  []
  (drop-table :friend-request))