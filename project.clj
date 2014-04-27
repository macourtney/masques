(defproject masques "1.0.0-SNAPSHOT"
  :description "Masques is a distributed social network."
  :dependencies [[clojure-tools "1.1.3-SNAPSHOT"]
                 [clj-crypto "1.0.1-SNAPSHOT"
                   :exclusions [bouncycastle/bcprov-jdk16]]
                 [clj-i2p "1.0.0-SNAPSHOT"]
                 [clj-internationalization "1.0.1-SNAPSHOT"
                   :exclusions [org.clojure/clojure]]
                 [clj-record "1.1.0"
                   :exclusions [org.clojure/clojure]]
                 [clj-time "0.5.0"]
                 [com.github.sarxos/windows-registry-util "0.2"]
                 [com.h2database/h2 "1.3.174"]
                 [drift "1.5.1"]
                 [image-resizer "0.1.6"]
                 [korma "0.3.0-beta7"
                   :exclusions [org.clojure/clojure]]
                 [log4j/log4j "1.2.16"]
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/data.xml "0.0.4"]
                 [org.clojure/tools.cli "0.2.1"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.drift-db/drift-db-h2 "1.1.7-SNAPSHOT"]
                 [seesaw "1.4.1"]]

  :plugins [[lein-libdir "0.1.0"]
            [drift "1.5.1"]]

  :resource-paths ["pkg/resources"]

  :libdir-path "lib"

  :main masques.main
  
  :aot [masques.main]

  ; To run in development mode use: lein development
  :aliases { "development" ["run" "-m" "masques.development-main"]
             "dev" ["run" "-m" "masques.development-main"]}

  ;:offline? true
)
