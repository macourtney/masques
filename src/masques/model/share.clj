(ns masques.model.share
  (:use masques.model.base
        korma.core))

(defn save [record]
  (insert-or-update share record))

(defn get-message [message-id]
  (find-by-id message message-id))

(defn attach-message [share-record]
  (if (or (not (:message-id share-record)) (:message share-record))
  	share-record
    (assoc share-record :message (get-message (:message-id share-record)))))

(defn get-type [share-record]
  (upper-case (:content-type share-record)))

(defn get-content [share-record]
  (find-by-id (get-type share-record) (:content-id share-record)))

(defn attach-content [share-record]
  (assoc share-record :content (get-content share-record)))

(defn build [share-record]
  (let [with-content (attach-content share-record)]
  	(if (:message-id share-record) (attach-message share-record) with-content)))

(defn get-and-build [share-id]
  (build (find-by-id share share-id)))
