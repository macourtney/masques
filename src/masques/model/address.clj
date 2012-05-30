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
  [identity address]
  (if-let [address-record (first-identity-address identity)]
    (update { :id (:id address-record) :address address })
    (insert { :identity_id (:id identity) :address address })))

(defn save-or-update-current-identity-address
  "If there is already an address associated with the current identity, then this function replaces the address with the
given address. Otherwise, this function adds the given address to the database for the current identity."
  [address]
  (save-or-update-identity-address (identity/current-user-identity) address))