(ns masques.development-main
  (:require [clojure.tools.cli :as cli]
            [masques.core :as core]
            [masques.main :as main]))

(defn parse-arguments
  "Parses the given arguments. The only supported argument is --mode which sets the mode to development, production, or test."
  [args]
  (cli/cli args
    ["-m" "--mode" "The server mode. For example, development, production, or test." :default "development"]))

(defn -main [& args]
  (let [[args-map remaining help] (parse-arguments args)]
    (apply main/-main "-mode" (core/arg-mode args-map) remaining)
    @(promise)))