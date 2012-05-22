(ns config.environment
  (:require [clojure.tools.logging :as logging]
            [masques.uncaught-exception-handler :as uncaught-exception-handler]))

(def initialized? (atom false))

(def conjure-environment-property "conjure.environment")
(def default-environment "production")

(defn
  set-evironment-property [environment]
  (System/setProperty conjure-environment-property environment)) 

(defn
  require-environment []
  (when (not (System/getProperty conjure-environment-property))
    (set-evironment-property default-environment))
  (let [mode (System/getProperty conjure-environment-property)]
    (require (symbol (str "config.environments." mode)))))

(defn
#^{ :doc "Returns the name of the environment." }
  environment-name []
  (System/getProperty conjure-environment-property))

(defn environment-init []
  (when (compare-and-set! initialized? false true)
    (uncaught-exception-handler/init)
    (require-environment)))