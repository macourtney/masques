(ns masques.model.group-permission
  (:require [clj-record.boot :as clj-record-boot]
            [masques.model.group :as group-model]
            [masques.model.permission :as permission-model])
  (:use masques.model.base))

(clj-record.core/init-model
  (:associations (belongs-to group)
                 (belongs-to permission)))

(defn add-permission-to-group [group permission]
  (insert { :group_id (group-model/group-id group) :permission_id (permission-model/permission-id permission) }))

(defn has-permission? [group permission]
  (find-record { :group_id (group-model/group-id group) :permission_id (permission-model/permission-id permission) }))

(defn remove-permission-from-group [group permission]
  (when-let [group-permission (has-permission? group permission)]
    (destroy-record group-permission)))