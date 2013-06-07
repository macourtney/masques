(ns masques.model.profile
  (:use masques.model.base
        korma.core))

(defn save [record]
  (insert-or-update profile record))
