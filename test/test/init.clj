(ns test.init
  (:import [java.io File])
  (:require [masques.core :as masques-core]
            [drift.runner :as drift-runner]))

(def test-init? (atom false)) 

(defn
  init-tests []
  (when (compare-and-set! test-init? false true)
    (println "Initializing test database.")
    (masques-core/set-mode "test")
    (masques-core/environment-init)
    (drift-runner/update-to-version 0) ; Reset the test database
    (drift-runner/update-to-version Integer/MAX_VALUE)
    (masques-core/database-init)))

(init-tests)