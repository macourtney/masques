(ns masques.model.group-membership
  (:require [clj-record.boot :as clj-record-boot])
  (:use masques.model.base))

(clj-record.core/init-model
  (:associations (belongs-to group)
                 (belongs-to friend)))

(defn add-friend-to-group [^Integer friend-id ^Integer group-id]
  (insert { :group_id group-id :friend_id friend-id }))

(defn group-member? [^Integer friend-id ^Integer group-id]
  (find-record { :group_id group-id :friend_id friend-id }))

(defn group-ids [^Integer friend-id]
  (map :group_id (find-records { :friend_id friend-id })))

(defn remove-friend-from-group [^Integer friend-id ^Integer group-id]
  (when-let [group-membership (group-member? friend-id group-id)]
    (destroy-record group-membership)))