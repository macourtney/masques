(ns masques.initialization
  (:require [clj-i2p.server :as clj-i2p-server]
            [masques.model.peer :as peer-model]))

(defn init
  "Initializes the entire masques system excluding any database stuff, which should already be initialized. This
function should be called after a successful login."
  []
  (peer-model/init)
  (clj-i2p-server/init))