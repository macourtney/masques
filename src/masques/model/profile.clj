(ns masques.model.profile
  (:require [clj-crypto.core :as clj-crypto]
            [masques.model.avatar :as avatar-model])
  (:use masques.model.base
        korma.core)
  (:import [org.apache.commons.codec.binary Base64]))

; SAVE PROFILE

(defn name-avatar [profile-record]
  (str (:alias profile-record) "'s Avatar"))

(defn insert-avatar [profile-record]
  (let [avatar-file-map { :path (:avatar-path profile-record) :name (name-avatar profile-record) }]
    (avatar-model/create-avatar-image (:avatar-path profile-record))
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

