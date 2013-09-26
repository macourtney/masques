(ns masques.main
  (:require [masques.core :as core])
  (:gen-class))

(defn create-choose-action [args]
  (fn []
    (core/init-args args)
    (let [login-frame-ns 'masques.controller.login.login]
      (require login-frame-ns)
      ((ns-resolve (find-ns login-frame-ns) 'show)))))

(defn -main
  [& args]
  (let [data-directory-choose-ns 'masques.controller.data-directory.choose]
    (require data-directory-choose-ns)
    ((ns-resolve (find-ns data-directory-choose-ns) 'show) (create-choose-action args))))