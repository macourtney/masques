(ns masques.uncaught-exception-handler
  (:require [clojure.tools.logging :as logging]))

(defn init []
  (Thread/setDefaultUncaughtExceptionHandler
    (reify Thread$UncaughtExceptionHandler
      (uncaughtException [this thread throwable]
        (logging/error throwable "Uncaught Exception:")))))