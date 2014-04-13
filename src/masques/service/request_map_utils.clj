(ns masques.service.request-map-utils
  ;(:require [masques.model.friend :as friend-model]
            ;[masques.model.identity :as identity-model])
  )

;(defn sender-identity [request-map]
;  (when-let [sending-user (:user request-map)]
;    (identity-model/find-identity (:name sending-user) (:public-key sending-user) (:public-key-algorithm sending-user))))

;(defn sender-friend? [request-map]
;  (friend-model/friend? (sender-identity request-map)))