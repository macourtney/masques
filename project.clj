(defproject masques "1.0.0-SNAPSHOT"
  :description "Masques is a distributed social network."
  :dependencies [[clojure-tools "1.1.0-SNAPSHOT"]
                 [clj-record "1.1.0"]
                 [com.h2database/h2 "1.3.162"]
                 [drift "1.4.2-SNAPSHOT"]
                 [log4j/log4j "1.2.16"]
                 [org.clojure/clojure "1.2.1"]
                 [org.drift-db/drift-db-h2 "1.0.4"]
                 [org.clojars.macourtney/i2p "0.8.7-0"]
                 [org.clojars.macourtney/mstreaming "0.8.7-0"]
                 [org.clojars.macourtney/streaming "0.8.7-0"]
                 [seesaw "1.2.1"]]
  :dev-dependencies [[drift "1.4.2-SNAPSHOT"]
                     [org.clojure/clojure "1.2.1"]])