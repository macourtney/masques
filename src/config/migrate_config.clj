(ns config.migrate-config
  (:require [masques.core :as core]
            [drift.builder :as builder]
            [drift-db.migrate :as drift-db-migrate]))

(defn migrate-config []
   { :directory "/src/masques/database/migrations"
     :init core/init-args
     :ns-content "\n  (:use drift-db.core)"
     :migration-number-generator builder/timestamp-migration-number-generator
     :current-version drift-db-migrate/current-version
     :update-version drift-db-migrate/update-version })