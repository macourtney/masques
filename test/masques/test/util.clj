(ns masques.test.util
  (:require [masques.model.user :as user-model]))

(defn login []
  (when-not (user-model/login "test-user" (.toCharArray "password"))
    (throw (RuntimeException. "Failed to login as a test user."))))

(defn logout []
  (user-model/logout))

(defn login-fixture [function]
  (try
    (login) 
    (function)
    (finally
      (logout))))