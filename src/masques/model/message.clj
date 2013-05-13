(ns masques.model.message
  (:use masques.model.base
        korma.core))

(defn save [record]
  (insert-or-update message record))
