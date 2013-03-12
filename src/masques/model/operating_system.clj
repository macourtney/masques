(ns masques.model.operating-system
  (:require [clojure.string :as string]))

(def os-name-property "os.name")
(def operating-system (string/lower-case (System/getProperty os-name-property)))

(defn os-contains? [string]
  (>= (.indexOf operating-system string) 0))

(defn windows? []
  (os-contains? "win"))

(defn mac? []
  (os-contains? "mac"))

(defn unix? []
  (or (os-contains? "nix") (os-contains? "nux") (os-contains? "aix")))

(defn solaris? []
  (os-contains? "sunos"))