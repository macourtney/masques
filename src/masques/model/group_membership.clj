(ns masques.model.group-membership
  (:require [clj-record.boot :as clj-record-boot]
            [masques.model.friend :as friend-model]
            [masques.model.group :as group-model])
  (:use masques.model.base))

(clj-record.core/init-model
  (:associations (belongs-to group)
                 (belongs-to friend)))

(defn add-friend-to-group [group friend]
  (insert { :group_id (group-model/group-id group) :friend_id (friend-model/friend-id friend) }))

(defn group-member? [group friend]
  (find-record { :group_id (group-model/group-id group) :friend_id (friend-model/friend-id friend) }))

(defn remove-friend-from-group [group friend]
  (when-let [group-membership (group-member? group friend)]
    (destroy-record group-membership)))

