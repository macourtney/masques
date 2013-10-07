(ns masques.model.profile
  (:require [clj-crypto.core :as clj-crypto]
            [config.db-config :as db-config])
  (:use masques.model.base
        korma.core)
  (:import [org.apache.commons.codec.binary Base64]))

(def saved-current-user (atom nil))

; CURRENT USER

(defn current-user
  "Returns the currently logged in user or nil if no user is logged in."
  []
  @saved-current-user)
  
(defn set-current-user
  "Sets the currently logged in user."
  [profile]
  (reset! saved-current-user profile))
  
; SAVE PROFILE

(defn name-avatar [profile-record]
  (str (:alias profile-record) "'s Avatar"))

(defn insert-avatar [profile-record]
  (let [avatar-file-map { :path (:avatar-path profile-record) :name (name-avatar profile-record) }]
    (insert-or-update file avatar-file-map)))

(defn save-avatar [profile-record]
  (if (:avatar-path profile-record)
    (merge profile-record { :avatar-file-id (:id (insert-avatar profile-record)) })
    profile-record))

(defn save [record]
  (insert-or-update profile (dissoc (save-avatar record) :avatar :avatar-path)))

; BUILD PROFILE

(defn attach-avatar [profile-record]
  (if (:avatar-file-id profile-record)
    (conj { :avatar (find-by-id file (:avatar-file-id profile-record)) } profile-record)
    profile-record))

(defn build [id]
  (attach-avatar (find-by-id profile id)))

; CREATE USER

(defn generate-keys [profile-record]
  (let [key-pair (clj-crypto/generate-key-pair)
        key-pair-map (clj-crypto/get-key-pair-map key-pair)]
    (merge profile-record { :identity (Base64/encodeBase64String (:bytes (:public-key key-pair-map)))
                            :identity-algorithm (:algorithm (:public-key key-pair-map))
                            :private-key (Base64/encodeBase64String (:bytes (:private-key key-pair-map)))
                            :private-key-algorithm (:algorithm (:private-key key-pair-map)) })))

(defn create-user [user-name]
  (save (generate-keys {:alias user-name})))

(defn find-logged-in-user
  "Finds the profile for the given user name which is a user of this database."
  [user-name]
  (when user-name
    (clean-up-for-clojure (first (filter :private-key (select profile (where { :alias user-name })))))))
    
(defn init
  "Loads the currently logged in user's profile into memory. Creating the profile if it does not alreay exist."
  []
  (let [user-name (db-config/current-username)]
    (if-let [user-profile (find-logged-in-user user-name)]
      (set-current-user user-profile)
      (do
        (create-user user-name)
        (recur)))))