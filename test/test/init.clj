(ns test.init
  (:import [java.io File])
  (:require [clj-i2p.core :as clj-i2p]
            [config.db-config :as db-config]
            [drift.runner :as drift-runner]
            [masques.core :as masques-core]))

(def test-destination (clj-i2p/as-destination "gfgHVoVFMYpBJwuL04mRa-~vQBt-p5lYfyVW2JatrzuJBy3Z7DgkxF68zDQe4M9uD-zOoBCBWctioFoUjnzPbDbflacwPvLnNxN-2GB64b73vDNPKkffM1JXLn6cRLWurxTYVeXaZns7ZmVj969XM3tOwEny1JZbMm-24YIaUwb66vkLeM33uanMer~II--OuikXx654ZkMXAORoJSu3hb04Q2s8sMR6-dnABeijfKShzINDg-JZSCRxWIay~VidFF6nhpi-BO3HPHPfGYTPkN5-w08z0IEaTeoBLNBBfVrmXwy2xPQWK1px2IRMpf0J~EiOf300Gin9xEoAhjEeL0LtUshT4bX2J1c~WiMHNGRJfjw4YspNVr8sDLOcVziOshLORlYDwkV6~ZNmovRQdcnwQ9OZzfM16fYib7Xb2wVtk-5TGIJOJiaegazOIb7Ze71N~EGX6epvwU1m2eGZ2I6oe~i2MOekHmnqhvolC1MQTUNGi-temb17xaeomMAdAAAA"))

(def test-init? (atom false)) 

(defn
  init-tests []
  (when (compare-and-set! test-init? false true)
    (println "Initializing test database.")
    (masques-core/set-mode "test")
    (db-config/update-username-password "test" "password")
    (masques-core/database-init)
    (drift-runner/update-to-version 0) ; Reset the test database
    (drift-runner/update-to-version Long/MAX_VALUE)
    (masques-core/run-fn 'masques.model.peer 'init)
    (clj-i2p/set-destination test-destination)))

(init-tests)