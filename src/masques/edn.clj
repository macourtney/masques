(ns masques.edn
  (:refer-clojure :exclude [read read-string])
  (:require [clojure.edn :as edn]
            [clojure.java.io :as java-io])
  (:import [java.io PushbackReader StringReader StringWriter]))

(defn write-out
  "Writes the given forms to standard out."
  [& forms]
  (binding [*print-dup* true]
    (doseq [form forms]
      (println form))))

(defn write
  "Writes the given forms to the given out. Out must be something which can be turned into a java.io.Writer."
  [out & forms]
  (binding [*out* (java-io/writer out)]
    (apply write-out forms)))

(defn write-string
  "Writes the given forms as a string and returns the results."
  [& forms]
  (let [string-writer (new StringWriter)]
    (apply write string-writer forms)
    (.toString string-writer)))

(defn read
  "Reads in in using edn and returns the results."
  ([in] (read in {}))
  ([in options]
    (edn/read options (PushbackReader. (java-io/reader in)))))

(defn read-string
  "Reads the string in using edn and returns the result."
  ([in] (read-string in {}))
  ([in options]
    (read (StringReader. in) options)))