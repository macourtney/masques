(ns masques.model.profile
  (:require [clj-crypto.core :as clj-crypto])
  (:use masques.model.base
        korma.core)
  (:import [org.apache.commons.codec.binary Base64]))

; SAVE PROFILE

(defn save-avatar [profile-record]
  (if (:avatar-path profile-record)
    (let [avatar (insert-or-update file { :path (:avatar-path profile-record) :name "Avatar" })]
      (merge profile-record { :avatar-file-id (:id avatar) }))
    profile-record))

(defn save [record]
  (insert-or-update profile (dissoc (save-avatar record) :avatar :avatar-path)))

; BUILD PROFILE

(defn attach-avatar [profile-record]
  (cond
    (:avatar profile-record)
      profile-record
    (:avatar-file-id profile-record)
      (conj { :avatar (find-by-id file (:avatar-file-id profile-record)) } profile-record)
    :else profile-record))

(defn build [id]
  (let [profile-record (find-by-id profile id)]
    (attach-avatar profile-record)))

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

