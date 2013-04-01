(ns masques.model.file
  (:use masques.model.base
        korma.core))

(defn save [record]
  (insert-or-update file record))