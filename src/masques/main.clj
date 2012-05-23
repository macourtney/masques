(ns masques.main
  (:require [masques.core :as core])
  (:gen-class))

(defn -main
  [& args]
  (core/init-args args)
  ;(let [login-frame-ns 'darkexchange.controller.login.login]
  ;  (require login-frame-ns)
  ;  ((ns-resolve (find-ns login-frame-ns) 'show)))
  )