(ns masques.model.message
  (:use masques.model.base
        korma.core))

(defn find-message
  "Finds the message with the given id."
  [message-id]
  (find-by-id message message-id))

(defn save [record]
  (insert-or-update message record))

(defn delete-message [record]
  (delete-record message record))

(defn create-message
  "Creates a message from the given message string."
  [message-str]
  (insert-or-update message { :body message-str }))

(defn find-or-create
  "Finds or creates a message from the given message. If message is a string,
then a message is created and returned. If the message is map, then the id is
used to get a fresh copy from the database."
  [message]
  (cond
    (string? message) (create-message message)
    (map? message) (find-message (id message))
    :else (throw (RuntimeException. (str "Unknown message type: " message) ))))