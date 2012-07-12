(ns masques.interceptor
  (:require [masques.model.record-utils :as record-utils]
            [masques.model.user :as user-model]))

(defn cleaned-current-user []
  (record-utils/clean-keys (user-model/clean-private-data)))

(defn interceptor [function request-map]
  (assoc (function request-map) :user (cleaned-current-user)))