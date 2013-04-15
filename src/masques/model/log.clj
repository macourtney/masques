(ns masques.model.log
  (:use masques.model.base
        korma.core))

(defn save [record]
  (insert-or-update log record))
