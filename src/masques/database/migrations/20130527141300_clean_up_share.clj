(ns masques.database.migrations.20130527141300-clean-up-share
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Deletes fields that are duplicative of profile fields. Add profile id."
  []
  (drop-column :share :identity_to)
  (drop-column :share :identity_to_algorithm)
  (drop-column :share :identity_from)
  (drop-column :share :identity_from_algorithm)
  (add-column :share (integer :profile_to_id))
  (add-column :share (integer :profile_from_id)))
  
(defn down
  "Changes the name of the table back to friend, from friend_request"
  []
  (drop-column :share :profile_to_id)
  (drop-column :share :profile_from_id)
  (add-column :share (text :identity_to))
  (add-column :share (string :identity_to_algorithm))
  (add-column :share (text :identity_from))
  (add-column :share (string :identity_from_algorithm)))

