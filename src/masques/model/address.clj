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