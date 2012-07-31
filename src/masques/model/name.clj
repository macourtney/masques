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

(defn save-or-update-identity-name
  "If there is already a name for the given identity, then this function replaces the name with the given name.
Otherwise, this function adds the given name to the database."
  [identity name]
  (if-let [name-record (first-identity-name identity)]
    (update { :id (:id name-record) :name name })
    (insert { :identity_id (:id identity) :name name })))

(defn save-or-update-current-identity-name
  "If there is already a name associated with the current identity, then this function replaces the name with the given
name. Otherwise, this function adds the given name to the database for the current identity."
  [name]
  (save-or-update-identity-name (identity/current-user-identity) name))

(defn find-name [name]
  (find-record { :name name }))

(defn find-name-identity [name]
  (cond
    (string? name) (find-name-identity (find-name name))
    (map? name) (identity/find-record { :id (:identity_id name) })
    (integer? name) (find-name-identity (get-record name))
    :else (throw (RuntimeException. (str "Don't know how to get an identity for type: " (type name))))))