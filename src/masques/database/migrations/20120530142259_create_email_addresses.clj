(ns masques.database.migrations.20120530142259-create-email-addresses
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Creates the email_addresses table."
  []
  (create-table :email_addresses
    (id)
    (string :email_address)
    (belongs-to :identity)))

(defn down
  "Deletes the email_addresses table."
  []
  (drop-table :email_addresses))