(ns masques.edn
  (:refer-clojure :exclude [read read-string])
  (:require [clojure.edn :as edn]
            [clojure.java.io :as java-io]
            [clojure.tools.string-utils :as string-utils])
  (:import [java.io PushbackReader StringReader StringWriter]))

(defn write-out
  "Writes the given forms to standard out."
  [& forms]
  (doseq [form forms]
    (println (string-utils/form-str form))))

(defn write
  "Writes the given forms to the given out. Out must be something which can be turned into a java.io.Writer."
  [out & forms]
  (with-open [out-writer (java-io/writer out)]
    (binding [*out* out-writer]
      (apply write-out forms))))

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
    (with-open [in-reader (java-io/reader in)]
      (edn/read options (PushbackReader. in-reader)))))

(defn read-string
  "Reads the string in using edn and returns the result."
  ([in] (read-string in {}))
  ([in options]
    (read (StringReader. in) options)))