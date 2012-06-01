(ns masques.model.friend
  (:require [clj-record.boot :as clj-record-boot]
            [masques.model.identity :as identity])
  (:use masques.model.base))

(clj-record.core/init-model
  (:associations (belongs-to identity)
                 (belongs-to friend :fk friend_id :model identity)))

(defn all-friends
  "Returns all of the friends for the current identity"
  []
  (find-records { :identity_id (:id (identity/current-user-identity)) }))