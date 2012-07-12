(ns masques.interceptor
  (:require [clj-i2p.server-interceptors :as server-interceptors]
            [masques.model.record-utils :as record-utils]
            [masques.model.user :as user-model]))

(defn cleaned-current-user []
  (record-utils/clean-keys (user-model/clean-private-data)))

(defn interceptor [function request-map]
  (assoc (function request-map) :user (cleaned-current-user)))

(defn init []
  (server-interceptors/add-interceptor interceptor))