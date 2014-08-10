(ns masques.interceptor
  (:require [clj-i2p.server-interceptors :as server-interceptors]
            [clojure.tools.logging :as logging]
            [masques.model.profile :as profile]))

(defn cleaned-current-user []
  (profile/clean-user-data))

(defn interceptor [function request-map]
  (assoc (function request-map) :user (cleaned-current-user)))

(defn init []
  (server-interceptors/add-interceptor interceptor))