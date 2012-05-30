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