(defproject masques "1.0.0-SNAPSHOT"
  :description "Masques is a distributed social network."
  :dependencies [[clojure-tools "1.1.3-SNAPSHOT"]
                 [clj-crypto "1.0.1-SNAPSHOT"]
                 [clj-i2p "1.0.0-SNAPSHOT"]
                 [clj-internationalization "1.0.1-SNAPSHOT"]
                 [clj-record "1.1.0"
                    :exclusions [org.clojure/clojure]]
                 [com.h2database/h2 "1.3.162"]
                 [drift "1.4.6-SNAPSHOT"]
                 [log4j/log4j "1.2.16"]
                 [org.clojure/clojure "1.4.0"]
                 [org.clojure/data.xml "0.0.4"
                    :exclusions [org.clojure/clojure]]
                 [org.clojure/tools.cli "0.2.1"
                    :exclusions [org.clojure/clojure]]
                 [org.clojure/tools.logging "0.2.3"
                    :exclusions [org.clojure/clojure]]
                 [org.drift-db/drift-db-h2 "1.1.5-SNAPSHOT"]
                 [seesaw "1.4.1"
                    :exclusions [org.clojure/clojure]]]

  :profiles { :dev { :dependencies [[drift "1.4.6-SNAPSHOT"]] } }

  :resource-paths ["pkg/resources"]

  :main masques.main

  ; To run in development mode use: lein development
  :aliases { "development" ["run" "-m" "masques.development-main"]
             "dev" ["run" "-m" "masques.development-main"]})