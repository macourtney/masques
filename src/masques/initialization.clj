(ns masques.initialization
  (:require [clj-i2p.server :as clj-i2p-server]
            [masques.model.identity :as identity-model]
            [masques.model.peer :as peer-model]
            [masques.service.protocol :as service-protocol]))

(defn init
  "Initializes the entire masques system excluding any database stuff, which should already be initialized. This
function should be called after a successful login."
  []
  (peer-model/init)
  (identity-model/init)
  (service-protocol/init)
  (clj-i2p-server/init))