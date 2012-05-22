(ns masques.core
  (:require [config.db-config :as db-config]
            [clojure.tools.cli :as cli]
            [clojure.tools.string-utils :as conjure-str-utils]
            [config.environment :as environment]
            [drift.runner :as drift-runner]
            [drift-db.core :as drift-db]))

(def database-initialized? (atom false))

(defn database-init []
  (when (compare-and-set! database-initialized? false true)
    (environment/environment-init)
    (drift-db/init-flavor (db-config/load-config))
    (drift-runner/update-to-version Integer/MAX_VALUE)))

(defn
#^{ :doc "Sets the server mode to the given mode. The given mode must be a keyword or string like development, 
production, or test." }
  set-mode [mode]
  (when mode 
    (environment/set-evironment-property (conjure-str-utils/str-keyword mode))))

(defn parse-arguments
  "Parses the given arguments. The only supported argument is --mode which sets the mode to development, production, or test."
  [args]
  (cli/cli args
    ["-m" "--mode" "The server mode. For example, development, production, or test." :default nil]))

(defn arg-mode [parsed-args]
  (get parsed-args :mode))

(defn init-args [args]
  (let [[args-map remaining help] (parse-arguments args)]
    (set-mode (arg-mode args-map))
    (database-init)
    [remaining help]))