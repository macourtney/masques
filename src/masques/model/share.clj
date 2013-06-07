(ns masques.model.share
  (:require [masques.model.message :as message-model]
            [clojure.tools.logging :as logging])
  (:use masques.model.base
        korma.core))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Message.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-message [message-id]
  (find-by-id message message-id))

(defn needs-message [share-record]
  (and (:message-id share-record) (not (:message share-record))))

(defn assoc-message [share-record]
  (assoc share-record :message (get-message (:message-id share-record))))

(defn attach-message [share-record]
  (if (needs-message share-record) (assoc-message share-record) share-record))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Content.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-type [share-record]
  (upper-case (:content-type share-record)))

(defn needs-content [share-record]
  (and (:content-id share-record) (not (:content share-record))))

(defn get-content [share-record]
  (find-by-id (get-type share-record) (:content-id share-record)))

(defn assoc-content [share-record]
  (assoc share-record :content (get-content share-record)))

(defn attach-content [share-record]
  (if (needs-content share-record) (assoc-content share-record) share-record))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Build/Save.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn build [share-record]
  (attach-message (attach-content share-record)))

(defn get-and-build [share-id]
  (build (find-by-id share share-id)))

(defn save-content [share-record]
  (insert-or-update (get-type share-record) (:content share-record)))

(defn save [record]
  (insert-or-update share record))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Sending and receiving of shares.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn is-friend-request [share-record]
  (= (get-type share-record) "FRIEND"))

(defn is-valid [share-record]
  (println (str "\nTesting if valid " share-record))
  true)

(defn is-from-friend [share-record]
  (println (str "\nTesting if from from friend " share-record))
  true)

(defn handle-friend-request [share-record]
  (println (str "\nThis is a friend request " share-record))
  true)

(defn accept [share-record]
  (println (str "\nWe are accepting the share record " share-record))
  true)

(defn reject [share-record]
  (println (str "A share record was rejected " share-record))
  (logging/error (str "\nA share record was rejected " share-record)))

(defn handle [share-record]
  (cond
    (is-friend-request share-record)
      (handle-friend-request share-record)
    (is-from-friend share-record)
      (accept share-record)
    :else (reject share-record)))

(defn receive [share-record]
  (if (is-valid share-record) (handle share-record) (reject share-record)))

(defn delete-share [record]
  (delete-record share record))