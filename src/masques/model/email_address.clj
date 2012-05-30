(ns masques.model.email-address
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.tools.logging :as logging]
            [masques.model.identity :as identity])
  (:use masques.model.base))

(clj-record.core/init-model
  (:associations (belongs-to identity)))

(defn first-identity-email-address [identity]
  (find-record { :identity_id (:id identity) }))

(defn first-current-identity-email-address []
  (first-identity-email-address (identity/current-user-identity)))

(defn save-or-update-identity-email-address
  "If there is already an email address for the given identity, then this function replaces the email address with the
given email address. Otherwise, this function adds the given email address to the database."
  [identity email-address]
  (if-let [name-record (first-identity-email-address identity)]
    (update { :id (:id name-record) :email_address email-address })
    (insert { :identity_id (:id identity) :email_address email-address })))

(defn save-or-update-current-identity-email-address
  "If there is already an email address associated with the current identity, then this function replaces the email
address with the given email address. Otherwise, this function adds the given email address to the database for the
current identity."
  [email-address]
  (save-or-update-identity-email-address (identity/current-user-identity) email-address))