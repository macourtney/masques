(ns masques.database.migrations.20130329012500-drop-email-addresses
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Deletes the email_addresses table."
  []
  (drop-table :email_addresses))

(defn down
  "Creates the email_addresses table."
  []
  (create-table :email_addresses
    (id)
    (string :email_address)
    (belongs-to :identity)))

