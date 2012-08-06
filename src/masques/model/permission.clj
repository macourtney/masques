(ns masques.model.permission
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.string :as string])
  (:use masques.model.base))

(def profile-name-permission "profile-name")
(def profile-email-permission "profile-email")
(def profile-phone-number-permission "profile-phone-number")
(def profile-address-permission "profile-address")
(def profile-country-permission "profile-country")
(def profile-province-permission "profile-province")
(def profile-city-permission "profile-city")
(def profile-postal-code-permission "profile-postal-code")

(clj-record.core/init-model
  (:associations (has-many group-permissions)))

(defn find-permission [permission]
  (cond
    (string? permission) (find-record { :name permission })
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

(defn find-permissions [permission-ids]
  (find-by-sql [(str "SELECT * FROM permissions WHERE id IN " (sql-list (filter-ids permission-ids)))]))