(ns masques.model.share-profile
  (:require [clj-time.core :as clj-time]
            [clojure.tools.logging :as logging]
            [korma.core :as korma]
            [masques.model.grouping :as grouping-model]
            [masques.model.grouping-profile :as grouping-profile-model]
            [masques.model.profile :as profile-model])
  (:use masques.model.base
        korma.core))

(def group-id-key :group-id)
(def profile-to-id-key :profile-to-id)
(def share-id-key :share-id)
(def shown-in-stream-at-key :shown-in-stream-at)
(def transferred-at-key :transferred-at)

(defn save
  "Saves the given profile record to the database."
  [record]
  (insert-or-update share-profile record))

(defn share-id
  "Returns the share id in the given profile share."
  [profile-share]
  (share-id-key profile-share))

(defn find-share-profile
  "Finds the share-profile with the given record. If the record is an integer or
includes an id, then the share-profile is found by id. Otherwise, the record is
used as a prototype."
  [record]
  (if (or (integer? record) (id record))
    (find-by-id share-profile record)
    (find-first share-profile record)))

(defn delete-share-profile
  "Deletes the given share-profile record."
  [record]
  (delete-record share-profile record))

(defn create-share-profile
  "Creates a new share-profile for the given share and profile. If the profile
was added as part of group, then the group is added."
  ([share-id profile-id]
    (create-share-profile share-id profile-id nil))
  ([share-id profile-id group-id]
    (create-share-profile share-id profile-id group-id nil))
  ([share-id profile-id group-id transferred-at]
    (when-let [share-id (id share-id)]
      (when-let [profile-id (id profile-id)]
        (save { share-id-key share-id
                profile-to-id-key profile-id
                group-id-key group-id
                transferred-at-key transferred-at })))))

(defn update-transferred-at-to-now
  "Updates the transferred at value to now for the share-profile with the given
share and profile."
  [share-id profile-id]
  (when-let [share-profile-to-update (find-share-profile
                                       { share-id-key (id share-id)
                                         profile-to-id-key (id profile-id) })]
    (update-record share-profile
      { id-key (id share-profile-to-update)
        transferred-at-key (clj-time/now) })))

(defn create-all-share-profiles-for-group
  "Creates share-profiles for the given share and group. One share profile is
created for each profile in the given group."
  [share-id group-id]
  (doseq [profile-id (grouping-profile-model/profile-ids group-id)]
    (create-share-profile share-id profile-id group-id)))

(defn ids-for-share
  "Returns all of the share profile ids for the share with the given id."
  [share-id]
  (find-all-ids share-profile { share-id-key (id share-id) }))

(defn profile-ids-for-share
  "Returns all of the profile ids for the given share."
  [share-id]
  (map profile-to-id-key
    (select
      share-profile
      (fields (h2-keyword profile-to-id-key))
      (where { (h2-keyword share-id-key) (id share-id) }))))

(defn find-current-share-profile
  "Returns the share profile with the given share id and the current profile
id."
  [share-id]
  (first
    (select
      share-profile
      (fields id-key)
      (where { (h2-keyword share-id-key) (id share-id) }))))

(defn first-profile-id-for-share
  "Returns the first profile id for the given share."
  [share-id]
  (profile-to-id-key
    (first
      (select
        share-profile
        (fields (h2-keyword profile-to-id-key))
        (where { (h2-keyword share-id-key) (id share-id) })
        (limit 1)))))

(defn share-ids-for-profile
  "Returns all of the share ids for the profile with the given id. If no profile
is given, then the current user is used."
  ([]
    (share-ids-for-profile (id (profile-model/current-user))))
  ([profile-id]
    (map share-id-key
         (select
           share-profile
           (fields (h2-keyword share-id-key))
           (where { (h2-keyword profile-to-id-key) (id profile-id) })))))

(defn delete-all
  "Deletes all of the share-profiles for the given share id."
  [share-id]
  (doseq [share-profile-id (ids-for-share share-id)]
    (delete-record share-profile share-profile-id)))