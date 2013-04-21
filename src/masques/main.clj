(ns masques.main
  (:require [masques.core :as core]
            [masques.controller.data-directory.choose :as data-directory-choose])
  (:gen-class))

(defn create-choose-action [args]
  (fn []
    (core/init-args args)
    (let [login-frame-ns 'masques.controller.login.login]
      (require login-frame-ns)
      ((ns-resolve (find-ns login-frame-ns) 'show)))))

(defn -main
  [& args]
  (data-directory-choose/show (create-choose-action)))