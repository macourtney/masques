(ns masques.model.group
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.string :as string]
            [masques.model.identity :as identity]
            [masques.model.group-permission :as group-permission]
            [masques.model.permission :as permission])
  (:use masques.model.base))

(def full-read-permissions [permission/profile-name-permission permission/profile-email-permission
                            permission/profile-phone-number-permission permission/profile-address-permission
                            permission/profile-country-permission permission/profile-province-permission
                            permission/profile-city-permission permission/profile-postal-code-permission])

(def acquaintances-group { :name "Acquaintances" :read [permission/profile-name-permission] })
(def classmates-group { :name "Classmates" :read full-read-permissions })
(def coworkers-group { :name "Coworkers" :read full-read-permissions })
(def enemies-group { :name  "Enemies"
                     :read [permission/profile-name-permission]
                     :none [permission/profile-email-permission
                            permission/profile-phone-number-permission permission/profile-address-permission
                            permission/profile-country-permission permission/profile-province-permission
                            permission/profile-city-permission permission/profile-postal-code-permission] })
(def family-group { :name "Family" :read full-read-permissions })
(def followers-group { :name "Followers" :read [permission/profile-name-permission] })
(def friends-group { :name "Friends" :read full-read-permissions })
(def public-group { :name "Public" :read [permission/profile-name-permission] })

(def default-groups { (:name acquaintances-group) acquaintances-group
                      (:name classmates-group) classmates-group
                      (:name coworkers-group) coworkers-group
                      (:name enemies-group) enemies-group
                      (:name family-group) family-group
                      (:name followers-group) followers-group
                      (:name friends-group) friends-group
                      (:name public-group) public-group})

(def group-add-listeners (atom []))

(def group-update-listeners (atom []))

(def group-delete-listeners (atom []))

(defn add-group-add-listener [listener]
  (swap! group-add-listeners conj listener))

(defn remove-group-add-listener [listener]
  (swap! group-add-listeners remove-listener listener))

(defn group-add [group]
  (doseq [listener @group-add-listeners]
    (listener group)))

(defn group-add-listener-count []
  (count @group-add-listeners))

(defn add-group-update-listener [listener]
  (swap! group-update-listeners conj listener))

(defn remove-group-update-listener [listener]
  (swap! group-update-listeners remove-listener listener))

(defn group-update [group]
  (doseq [listener @group-update-listeners]
    (listener group)))

(defn group-update-listener-count []
  (count @group-update-listeners))

(defn add-group-delete-listener [listener]
  (swap! group-delete-listeners conj listener))

(defn remove-group-delete-listener [listener]
  (swap! group-delete-listeners remove-listener listener))

(defn group-delete [group]
  (doseq [listener @group-delete-listeners]
    (listener group)))

(defn group-delete-listener-count []
  (count @group-delete-listeners))

(clj-record.core/init-model
  (:associations (has-many group-memberships)
                 (has-many group-permissions))
  (:callbacks (:after-update group-update)
              (:after-insert group-add)
              (:after-destroy group-delete)))

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

(defn find-groups [groups]
  (find-by-sql [(str "SELECT * FROM groups WHERE identity_id = ? AND id IN " (sql-list (filter-ids (map group-id groups))))
                (identity/current-user-identity-id)]))

(defn find-identity-groups
  "Returns all of the groups for the given identity. If the identity is not given, then this function returns all of the groups for the currently logged in user."
  ([] (find-identity-groups (identity/current-user-identity)))
  ([identity]
    (find-records { :identity_id (:id identity) })))

(defn add-read-permission [group permission]
  (group-permission/add-read-permission-to-group (group-id group) (permission/permission-id permission)))

(defn add-write-permission [group permission]
  (group-permission/add-write-permission-to-group (group-id group) (permission/permission-id permission)))

(defn add-none-permission [group permission]
  (group-permission/add-none-permission-to-group (group-id group) (permission/permission-id permission)))

(defn has-read-permission? [group permission]
  (group-permission/has-read-permission? (group-id group) (permission/permission-id permission)))

(defn has-write-permission? [group permission]
  (group-permission/has-write-permission? (group-id group) (permission/permission-id permission)))

(defn has-none-permission? [group permission]
  (group-permission/has-none-permission? (group-id group) (permission/permission-id permission)))

(defn remove-read-permission [group permission]
  (group-permission/remove-read-permission-from-group (group-id group) (permission/permission-id permission)))

(defn remove-write-permission [group permission]
  (group-permission/remove-write-permission-from-group (group-id group) (permission/permission-id permission)))

(defn remove-none-permission [group permission]
  (group-permission/remove-none-permission-from-group (group-id group) (permission/permission-id permission)))

(defn any-group-has-read-permission? [groups permission]
  (group-permission/any-group-has-read-permission? (map group-id groups) (permission/permission-id permission)))

(defn any-group-has-write-permission? [groups permission]
  (group-permission/any-group-has-write-permission? (map group-id groups) (permission/permission-id permission)))

(defn add-default-group [identity default-group]
  (let [group-id (insert { :name (:name default-group) :identity_id (:id identity) :user_generated 1 })]
    (doseq [read-permission (:read default-group)]
      (add-read-permission group-id read-permission))
    (doseq [write-permission (:write default-group)]
      (add-write-permission group-id write-permission))
    (doseq [none-permission (:none default-group)]
      (add-none-permission group-id none-permission))))

(defn add-default-groups [identity]
  (when (identity/find-user identity)
    (doseq [default-group (vals default-groups)]
      (add-default-group identity default-group))))

(defn add-group
  "Adds a new group with the given name."
  ([group-name] (add-group group-name (identity/current-user-identity)))
  ([group-name identity]
    (when (and identity group-name (not-empty group-name) )
      (insert { :name group-name :identity_id (:id identity) :user_generated 1 }))))

(defn remove-deleted-identity-groups [identity]
  (doseq [group (find-identity-groups identity)]
    (destroy-record group)))

(defn removed-deleted-group-permissions [group]
  (group-permission/remove-all-permissions-from-group (group-id group)))

(defn init []
  (add-group-delete-listener removed-deleted-group-permissions) 
  (identity/add-identity-add-listener add-default-groups)
  (identity/add-identity-delete-listener remove-deleted-identity-groups))