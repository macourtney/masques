(ns masques.model.record-utils
  (:require [clojure.tools.loading-utils :as loading-utils]))

(defn clean-key
  "Converts all underscores in the given key to dashes. Returns a keyword regardless of the type of key passed in."
  [key]
  (when key
    (keyword (loading-utils/underscores-to-dashes (name key)))))

(defn clean-keys
  "Converts all underscores in the keys of record to dashes."
  [record]
  (when record
    (reduce (fn [new-record [key value]] (assoc new-record (clean-key key) value)) {} record)))