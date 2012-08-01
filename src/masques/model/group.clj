(ns masques.model.group
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.string :as string]
            [masques.model.identity :as identity])
  (:use masques.model.base))

(clj-record.core/init-model
  (:associations (has-many group-memberships)
                 (has-many group-permissions)))

(defn find-group [group]
  (cond
    (string? group) (find-record { :name group :identity_id (identity/current-user-identity-id) })
    (map? group) (if-let [group-id (:id group)] (find-group group-id) (find-record group))
    (integer? group) (find-record { :id group })
    :else (throw (RuntimeException. (str "Don't know how to get a group for type: " (type group))))))

(defn group-id [group]
  (cond
    (integer? group) group
    (map? group) (if-let [group-id (:id group)] group-id (:id (find-group group)))
    :else (:id (find-group group))))

(defn filter-ids [id-list]
  (filter integer? id-list))

(defn sql-list [value-list]
  (str "(" (string/join "," value-list) ")"))

(defn find-groups [group-ids]
  (find-by-sql [(str "SELECT * FROM groups WHERE identity_id = ? AND id IN " (sql-list (filter-ids group-ids)))
                (identity/current-user-identity-id)]))