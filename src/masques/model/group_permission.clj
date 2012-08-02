(ns masques.model.group-permission
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.string :as string])
  (:use masques.model.base))

(def none-type "none")
(def read-type "read")
(def write-type "write")

(def valid-types #{ read-type write-type none-type })

(clj-record.core/init-model
  (:associations (belongs-to group)
                 (belongs-to permission)))

(defn valid-type? [^String type]
  (contains? valid-types type))

(defn add-permission-to-group [^Integer group-id ^Integer permission-id ^String type]
  (when (and group-id permission-id (valid-type? type))
    (insert { :group_id group-id :permission_id permission-id :type type })))

(defn add-none-permission-to-group [^Integer group-id ^Integer permission-id]
  (add-permission-to-group group-id permission-id none-type))

(defn add-read-permission-to-group [^Integer group-id ^Integer permission-id]
  (add-permission-to-group group-id permission-id read-type)) 

(defn add-write-permission-to-group [^Integer group-id ^Integer permission-id]
  (add-permission-to-group group-id permission-id write-type))

(defn find-permissions [^Integer group-id ^Integer permission-id]
  (find-records { :group_id group-id :permission_id permission-id }))

(defn has-none-permission? [^Integer group-id ^Integer permission-id]
  (find-record { :group_id group-id :permission_id permission-id :type none-type }))

(defn has-read-permission? [^Integer group-id ^Integer permission-id]
  (when (not (has-none-permission? group-id permission-id))
    (find-record { :group_id group-id :permission_id permission-id :type read-type })))

(defn has-write-permission? [^Integer group-id ^Integer permission-id]
  (when (not (has-none-permission? group-id permission-id))
    (find-record { :group_id group-id :permission_id permission-id :type write-type })))

(defn remove-none-permission-from-group [^Integer group-id ^Integer permission-id]
  (when-let [group-permission (has-none-permission? group-id permission-id)]
    (destroy-record group-permission)))

(defn remove-read-permission-from-group [^Integer group-id ^Integer permission-id]
  (when-let [group-permission (has-read-permission? group-id permission-id)]
    (destroy-record group-permission)))

(defn remove-write-permission-from-group [^Integer group-id ^Integer permission-id]
  (when-let [group-permission (has-write-permission? group-id permission-id)]
    (destroy-record group-permission)))

(defn remove-permissions-from-group [^Integer group-id ^Integer permission-id]
  (when-let [group-permissions (find-permissions group-id permission-id)]
    (doseq [group-permission group-permissions]
      (destroy-record group-permission))))

(defn filter-ids [id-list]
  (filter integer? id-list))

(defn sql-list [value-list]
  (str "(" (string/join "," value-list) ")"))

(defn any-group-has-permission? [group-ids ^Integer permission-id ^String type]
  (seq
    (find-by-sql
      [ (str "SELECT * FROM group_permissions WHERE permission_id = ? AND type = ? AND group_id IN " (sql-list (filter-ids group-ids)))
        permission-id type ])))

(defn any-group-has-none-permission? [group-ids ^Integer permission-id]
  (any-group-has-permission? group-ids permission-id none-type))

(defn any-group-has-read-permission? [group-ids ^Integer permission-id]
  (when (not (any-group-has-none-permission? group-ids permission-id))
    (any-group-has-permission? group-ids permission-id read-type)))

(defn any-group-has-write-permission? [group-ids ^Integer permission-id]
  (when (not (any-group-has-none-permission? group-ids permission-id))
    (any-group-has-permission? group-ids permission-id write-type)))