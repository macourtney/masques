(ns masques.model.log
  (:use masques.model.base
        korma.core))

(def log-ns "model.log")

(defn prepare-log [record]
  record)

(defentity log
  (prepare prepare-log)
  (transform clean-up-for-clojure)
  (table :LOG))

(defn save [record]
  (insert-or-update log record))
