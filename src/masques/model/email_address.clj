(ns masques.model.email-address
  (:require [clj-record.boot :as clj-record-boot]
            [masques.model.identity :as identity])
  (:use masques.model.base))

(clj-record.core/init-model
  (:associations (belongs-to identity)))

(defn first-identity-email-address [identity]
  (find-record { :identity_id (:id identity) }))

(defn first-current-identity-email-address []
  (first-identity-email-address (identity/current-user-identity)))