(ns masques.model.grouping-profile
  (:require [clj-internationalization.term :as term]
            [clojure.tools.logging :as logging]
            [korma.core :as korma]
            [masques.model.grouping :as grouping]
            [masques.model.profile :as profile])
  (:use masques.model.base
        korma.core))

(def created-at-key :created-at)
(def grouping-id-key :grouping-id)
(def profile-id-key :profile-id)

(defn find-grouping-profile
  "Returns the grouping profile with the given id."
  [grouping-profile-id]
  (when grouping-profile-id
    (find-by-id grouping-profile (id grouping-profile-id))))

(defn save
  "Saves the given grouping profile to the database."
  [record]
  (insert-or-update grouping-profile record))

(defn create-grouping-profile
  "Creates a new grouping linking the given group to the given profile. Both the
group and profile must contain ids. If not, then this function returns nil."
  [group profile]
  (when-let [group-id (id group)]
    (when-let [profile-id (id profile)]
      { grouping-id-key group-id profile-id-key profile-id })))

(defn delete-grouping-profile
  "Deletes the given grouping profile from the database."
  [record]
  (delete-record grouping-profile record))

(defn find-group
  "Returns the group for the given grouping profile."
  [record]
  (grouping/find-grouping (grouping-id-key record)))

(defn find-profile
  "Returns the profile for the given grouping profile."
  [record]
  (profile/find-profile (profile-id-key record)))

(defn add-grouping-profile-delete-interceptor
  "Adds the given group profile delete interceptor."
  [interceptor]
  (add-delete-interceptor grouping-profile interceptor))

(defn remove-grouping-profile-delete-interceptor
  "Removes the given group profile delete interceptor."
  [interceptor]
  (remove-delete-interceptor grouping-profile interceptor))

(defn add-grouping-profile-insert-interceptor
  "Adds the given group profile insert interceptor."
  [interceptor]
  (add-insert-interceptor grouping-profile interceptor))

(defn remove-grouping-profile-insert-interceptor
  "Removes the given group profile insert interceptor."
  [interceptor]
  (remove-insert-interceptor grouping-profile interceptor))

(defn add-grouping-profile-update-interceptor
  "Adds the given group profile update interceptor."
  [interceptor]
  (add-update-interceptor grouping-profile interceptor))

(defn remove-grouping-profile-update-interceptor
  "Removes the given group profile update interceptor."
  [interceptor]
  (remove-update-interceptor grouping-profile interceptor))

(defn count-grouping-profiles
  "Counts all of the grouping profiles for the given group."
  [group-id]
  (count-records
    grouping-profile { (h2-keyword grouping-id-key) (id group-id) }))

(defn find-table-grouping-profile
  "Returns a grouping profile with just the id, grouping-id and profile-id at
the given index."
  [group-id index]
  (when (and group-id index)
    (first
      (korma/select
        grouping-profile
        (korma/fields id-key (h2-keyword grouping-id-key)
                      (h2-keyword profile-id-key))
        (where { (h2-keyword grouping-id-key) (id group-id) })
        (korma/limit 1)
        (korma/offset index)
        (korma/order (h2-keyword profile-id-key) :ASC)))))

(defn table-index-of
  "Returns the index of the given grouping profile record in the list of
grouping profiles."
  ([record] (table-index-of record (grouping-id-key record)))
  ([record grouping-id]
    (index-of
      record
      (korma/select
        grouping-profile
        (korma/fields id-key)
        (where { (h2-keyword grouping-id-key) grouping-id })
        (korma/order (h2-keyword profile-id-key) :ASC)))))

(defn find-grouping-profiles-for-profile
  "Finds all of the grouping profiles with the given profile id."
  [profile-id]
  (when-let [profile-id (id profile-id)]
    (korma/select
      grouping-profile
      (korma/fields id-key)
      (where { (h2-keyword profile-id-key) profile-id }))))

(defn delete-grouping-profiles
  "Deletes all of the grouping profiles with the given profile id."
  [profile-id]
  (doseq [grouping-profile (find-grouping-profiles-for-profile profile-id)]
    (delete-grouping-profile grouping-profile)))