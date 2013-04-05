(ns masques.database.migrations.20120601135705-create-friends
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Creates the friends table."
  []
  (create-table :friends
    (id)
    (belongs-to :identity)
    (belongs-to :friend)))

(defn down
  "Drops the friends table."
  []
  (drop-table :friends))