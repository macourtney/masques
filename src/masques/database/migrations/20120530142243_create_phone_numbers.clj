(ns masques.database.migrations.20120530142243-create-phone-numbers
  (:refer-clojure :exclude [boolean])
  (:use drift-db.core))

(defn up
  "Creates the phone_numbers table."
  []
  (create-table :phone_numbers
    (id)
    (string :phone_number)
    (belongs-to :identity)))
  
(defn down
  "Deletes the phone_numbers table."
  []
  (drop-table :phone_numbers))