(defproject masques "1.0.0-SNAPSHOT"
  :description "Masques is a distributed social network."
  :dependencies [[clojure-tools "1.1.0-SNAPSHOT"]
                 [clj-crypto "1.0.1-SNAPSHOT"]
                 [clj-i2p "1.0.0-SNAPSHOT"]
                 [clj-internationalization "1.0.0"]
                 [clj-record "1.1.0"]
                 [com.h2database/h2 "1.3.162"]
                 [drift "1.4.4-SNAPSHOT"]
                 [log4j/log4j "1.2.16"]
                 [org.clojure/clojure "1.2.1"]
                 [org.clojure/tools.cli "0.2.1"]
                 [org.drift-db/drift-db-h2 "1.0.8-SNAPSHOT"]
                 [seesaw "1.2.1"]]
  :dev-dependencies [[drift "1.4.4-SNAPSHOT"]
                     [org.clojure/clojure "1.2.1"]]

  :resources-path "pkg/resources"

  :main masques.main
  
  :run-aliases { :development masques.development-main
                 :dev masques.development-main })