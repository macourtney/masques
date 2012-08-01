(ns masques.model.permission
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.string :as string]
            [masques.model.identity :as identity])
  (:use masques.model.base))

(clj-record.core/init-model
  (:associations (has-many group-permissions)))

(defn find-permission [permission]
  (cond
    (string? permission) (find-record { :name permission :identity_id (identity/current-user-identity-id) })
    (map? permission) (if-let [permission-id (:id permission)] (find-permission permission-id) (find-record permission))
    (integer? permission) (find-record { :id permission })
    :else (throw (RuntimeException. (str "Don't know how to get a permission for type: " (type permission))))))

(defn permission-id [permission]
  (cond
    (integer? permission) permission
    (map? permission) (if-let [permission-id (:id permission)] permission-id (:id (find-permission permission)))
    :else (:id (find-permission permission))))

(defn filter-ids [id-list]
  (filter integer? id-list))

(defn sql-list [value-list]
  (str "(" (string/join "," value-list) ")"))

(defn find-permissions [group-ids]
  (find-by-sql [(str "SELECT * FROM permissions WHERE identity_id = ? AND id IN " (sql-list (filter-ids group-ids)))
                (identity/current-user-identity-id)]))