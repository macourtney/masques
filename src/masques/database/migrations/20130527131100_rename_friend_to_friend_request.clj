(ns masques.database.migrations.20130527131100-rename-friend-to-friend-request
  (:refer-clojure :exclude [boolean byte-array])
  (:use drift-db.core))

(defn up
  "Changes the name of the table from friend to friend_request."
  []
  (create-table :friend_request
    (id)
    (date-time :created_at)
    (string :request_status)
    (date-time :requested_at) ; Notice that this item and the one below it now have better names.
    (date-time :request_approved_at)
    (belongs-to :profile)))
  
(defn down
  "Changes the name of the table back to friend, from friend_request"
  []
  (drop-table :friend_request))

