(ns masques.model.message
  (:use masques.model.base
        korma.core))

(def body-key :body)

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
  (insert-or-update message { body-key message-str }))

(defn update-message
  "Updates the body of the given message."
  [message-id body]
  (update-record message { id-key (id message-id) body-key body }))

(defn find-or-create
  "Finds or creates a message from the given message. If message is a string,
then a message is created and returned. If the message is map, then the id is
used to get a fresh copy from the database."
  [message]
  (cond
    (string? message) (find-message (create-message message))
    (map? message) (find-message (id message))
    :else (throw (RuntimeException. (str "Unknown message type: " message) ))))

(defn body
  "Returns the message body for the given message, if the given message is an
integer, then it is treated as the id for a message in the database."
  [message]
  (if (integer? message)
    (body (find-message message))
    (body-key message)))