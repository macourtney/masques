(ns masques.database.migrations.20130329012000-drop-phone-numbers
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Deletes the phone_numbers table."
  []
  (drop-table :phone_numbers))

(defn down
  "Creates the phone_numbers table."
  []
  (create-table :phone_numbers
    (id)
    (string :phone_number)
    (belongs-to :identity)))
  
