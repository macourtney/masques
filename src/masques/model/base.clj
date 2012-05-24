(ns masques.model.base
  (:require [clojure.string :as string]
            [clojure.tools.logging :as logging]
            [masques.core :as masques-core])
  (:import [java.sql Clob]))

(def db (deref masques-core/db))

(defn clob-string [clob]
  (when clob
    (let [clob-stream (.getCharacterStream clob)]
      (try
        (string/join "\n" (take-while identity (repeatedly #(.readLine clob-stream))))
        (catch Exception e
          (logging/error (str "An error occured while reading a clob: " e)))))))

(defn load-clob [clob]
  (clob-string clob))

(defn get-clob [record clob-key]
  (when-let [clob (clob-key record)]
    (when (instance? Clob clob)
      clob)))

(defn clean-clob-key [record clob-key]
  (if-let [clob (get-clob record clob-key)]
    (assoc record clob-key (load-clob clob))
    record))

(defn as-boolean [value]
  (and value (not (= value 0))))

(defn remove-listener [listeners listener-to-remove]
  (remove #(= listener-to-remove %) listeners))