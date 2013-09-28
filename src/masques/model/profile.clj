(ns masques.model.profile
  (:require [clj-record.boot :as clj-record-boot]
            [clj-crypto.core :as clj-crypto])
  (:use masques.model.base
        korma.core)
  (:import [org.apache.commons.codec.binary Base64]))

(defn save [record]
  (insert-or-update profile record))

(defn generate-keys [profile-record]
  (let [key-pair (clj-crypto/generate-key-pair)
        key-pair-map (clj-crypto/get-key-pair-map key-pair)]
    (merge profile-record { :identity (Base64/encodeBase64String (:bytes (:public-key key-pair-map)))
                            :identity-algorithm (:algorithm (:public-key key-pair-map))
                            :private-key (Base64/encodeBase64String (:bytes (:private-key key-pair-map)))
                            :private-key-algorithm (:algorithm (:private-key key-pair-map)) })))

(defn create-user [user-name]
  (save (generate-keys {:alias user-name})))