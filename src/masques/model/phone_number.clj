(ns masques.model.phone-number
  (:require [clj-record.boot :as clj-record-boot]
            [masques.model.identity :as identity])
  (:use masques.model.base))

(clj-record.core/init-model
  (:associations (belongs-to identity)))

(defn first-identity-phone-number [identity]
  (find-record { :identity_id (:id identity) }))

(defn first-current-identity-phone-number []
  (first-identity-phone-number (identity/current-user-identity)))

(defn save-or-update-identity-phone-number
  "If there is already a phone number for the given identity, then this function replaces the phone number with the
given phone number. Otherwise, this function adds the given phone number to the database."
  [identity phone-number]
  (if-let [phone-number-record (first-identity-phone-number identity)]
    (update { :id (:id phone-number-record) :phone_number phone-number })
    (insert { :identity_id (:id identity) :phone_number phone-number })))

(defn save-or-update-current-identity-phone-number
  "If there is already a phone number associated with the current identity, then this function replaces the phone number
with the given phone number. Otherwise, this function adds the given phone number to the database for the current
identity."
  [phone-number]
  (save-or-update-identity-phone-number (identity/current-user-identity) phone-number))