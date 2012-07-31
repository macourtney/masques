(ns masques.model.permission
  (:require [clj-record.boot :as clj-record-boot])
  (:use masques.model.base))

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