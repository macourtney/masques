(defproject masques "1.0.0-SNAPSHOT"
  :description "Masques is a distributed social network."
  :dependencies [[clojure-tools "1.1.3"]
                 [clj-crypto "1.0.2"]
                 [clj-i2p "1.0.0"]
                 [clj-internationalization "1.0.1"
                   :exclusions [org.clojure/clojure]]
                 [clj-record "1.1.0"
                   :exclusions [org.clojure/clojure]]
                 [clj-time "0.6.0"]
                 [com.github.sarxos/windows-registry-util "0.2"]
                 [com.h2database/h2 "1.3.174"]
                 [drift "1.5.1"]
                 [image-resizer "0.1.6"]
                 [korma "0.4.0"]
                 [log4j/log4j "1.2.16"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/data.xml "0.0.4"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [org.clojure/tools.cli "0.2.1"]
                 [org.clojure/tools.logging "0.3.0"]
                 [org.drift-db/drift-db-h2 "1.1.7"]
                 [seesaw "1.4.4"]]

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
