(ns masques.model.share
  (:use masques.model.base
        korma.core))

(defn save [record]
  (insert-or-update share record))
