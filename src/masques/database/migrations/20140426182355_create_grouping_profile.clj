(ns masques.database.migrations.20140426182355-create-grouping-profile
  (:use drift-db.core))

(defn up
  "Migrates the database up to version 20140426182355."
  []
  (create-table :grouping-profile
    (id)
    (date-time :created-at)
    (belongs-to :grouping)
    (belongs-to :profile)))

(defn down
  "Migrates the database down from version 20140426182355."
  []
  (drop-table :grouping-profile))