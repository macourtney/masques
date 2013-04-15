(ns masques.database.migrations.20130329013100-drop-friends
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Drops the friends table."
  []
  (drop-table :friends))

(defn down
  "Creates the friends table."
  []
  (create-table :friends
    (id)
    (belongs-to :identity)
    (belongs-to :friend)))

