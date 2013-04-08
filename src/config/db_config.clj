;; This file is used to configure the database and connection.

(ns config.db-config
  (:require [clojure.tools.logging :as logging]
            [config.environment :as environment]
            [drift-db-h2.flavor :as h2]))

(def data-directory (atom "data/db/"))

(defn data-dir []
  @data-directory)

(defn update-data-directory [new-data-dir]
  (reset! data-directory new-data-dir))

(defn dbname [environment]
  (cond
     ;; The name of the production database to use.
     (= environment :production) "masques_production"

     ;; The name of the development database to use.
     (= environment :development) "masques_development"

     ;; The name of the test database to use.
     (= environment :test) "masques_test"))

(defn
#^{:doc "Returns the database flavor which is used by Conjure to connect to the database."}
  create-flavor [environment]
  (logging/info (str "Environment: " environment))
  (h2/h2-flavor

    ;; Calculates the database to use.
    (dbname environment)

    @data-directory))

(defn
  load-config []
  (let [environment (environment/environment-name)]
    (if-let [flavor (create-flavor (keyword environment))]
      flavor
      (throw (new RuntimeException (str "Unknown environment: " environment ". Please check your conjure.environment system property."))))))
