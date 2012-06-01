(ns masques.model.address
  (:require [clj-record.boot :as clj-record-boot]
            [masques.model.identity :as identity])
  (:use masques.model.base))

(clj-record.core/init-model
  (:associations (belongs-to identity)))

(defn first-identity-address [identity]
  (find-record { :identity_id (:id identity) }))

(defn first-current-identity-address []
  (first-identity-address (identity/current-user-identity)))

(defn save-or-update-identity-address
  "If there is already an address for the given identity, then this function replaces the address with the given
address. Otherwise, this function adds the given address to the database."
  [identity address country province city postal-code]
  (let [base-record { :address address :country country :province province :city city :postal_code postal-code }]
    (if-let [address-record (first-identity-address identity)]
      (update (assoc base-record :id (:id address-record)))
      (insert (assoc base-record :identity_id (:id identity))))))

(defn save-or-update-current-identity-address
  "If there is already an address associated with the current identity, then this function replaces the address with the
given address. Otherwise, this function adds the given address to the database for the current identity."
  [address country province city postal-code]
  (save-or-update-identity-address (identity/current-user-identity) address country province city postal-code))