(ns masques.model.friend-request
  (:require [clj-time.core :as clj-time])
  (:use masques.model.base))

(defn set-requested-at [record]
  (if (or (:requested-at record) (:REQUESTED_AT record))
    record 
    (conj record {:REQUESTED_AT (str (clj-time/now))})))

(defn save [record]
  (let [clean (set-requested-at record)]
    (insert-or-update friend-request clean)))

(defn send-request [mid destination]
  ; We need to create a share, attach a friend request and profile to it.
  )