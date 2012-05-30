(ns masques.model.name
  (:require [clj-record.boot :as clj-record-boot]
            [masques.model.identity :as identity])
  (:use masques.model.base))

(clj-record.core/init-model
  (:associations (belongs-to identity)))

(defn first-identity-name [identity]
  (find-record { :identity_id (:id identity) }))

(defn first-current-identity-name []
  (first-identity-name (identity/current-user-identity)))