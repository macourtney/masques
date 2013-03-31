(ns masques.model.base
  (:use korma.core)
  (:require [clojure.string :as string]
            [clojure.tools.logging :as logging]
            [masques.core :as masques-core]
            [korma.db :as korma-db]
            [drift-db.core :as drift-db])
  (:import [java.sql Clob]))

; Database connections. db is for non-korma stuff.
(def db (deref masques-core/db))
(korma-db/defdb mydb (drift-db/db-map))

; Random helpers.
(defn as-boolean [value]
  (and value (not (= value 0))))

(defn remove-listener [listeners listener-to-remove]
  (remove #(= listener-to-remove %) listeners))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Clob stuff.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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

(defn clean-clob[record field-name field-data]
    (assoc record field-name (load-clob field-data)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Turn h2 field names (:KEYWORDS_THAT_LOOK_LIKE_THIS) into
; clojure-style names (:keywords-that-look-like-this)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn remove-colon [has-colon]
  (string/replace (string/lower-case has-colon) ":" ""))

(defn replace-underscores-with-hyphens [has-underscores]
  (string/replace has-underscores "_" "-"))

(defn replace-hyphens-with-underscores [has-hyphens]
  (string/replace has-hyphens "-" "_"))

(defn clojure-keyword [h2-keyword]
  (let [no-colon (remove-colon (str h2-keyword))
        no-underscores (replace-underscores-with-hyphens no-colon)
        lower-cased (string/lower-case no-underscores)]
    (keyword lower-cased)))

(defn h2-keyword [clojure-keyword]
  (let [no-colon (str (remove-colon clojure-keyword))
        no-hyphens (replace-hyphens-with-underscores no-colon)
        upper-cased (string/upper-case no-hyphens)]
    (keyword upper-cased)))

(defn remove-item [item record]
  (dissoc (into {} record) item))

(defn clean-field-data [record field-name]
  (let [field-data (get record field-name)]
    (cond
      (nil? field-data)
        (remove-item field-name record)
      (instance? Clob field-data)
        (clean-clob record field-name field-data)
      :else record)))

(defn clojure-field-name [record field-name]
  (let [field-data (get record field-name)
        ready-map (remove-item field-name record)]
    (assoc ready-map (clojure-keyword field-name)  field-data)))

(defn h2-field-name [record field-name]
  (let [field-data (get record field-name)
        ready-map (remove-item field-name record)]
    (assoc ready-map (h2-keyword field-name)  field-data)))

(defn clean-up-for-clojure [record]
  (let [clean-data (reduce clean-field-data record (keys record))]
    (reduce clojure-field-name clean-data (keys clean-data))))

(defn clean-up-for-h2 [record]
  (let [clean-data (reduce clean-field-data record (keys record))]
    (reduce h2-field-name clean-data (keys clean-data))))

(defn find-by-id [entity id]
  (clean-up-for-clojure (first (select entity (where {:ID id})))))

(defn insert-record [entity record]
  (let [id (vals (insert entity (values record)))]
    (find-by-id entity id)))

(defn update-record [entity record]
  (update entity
    (set-fields record)
    (where {:ID (:id record)})))

(defn insert-or-update [entity record]
  (let [h2-record (clean-up-for-h2 record)]
    (if (:ID h2-record)
      (update-record entity h2-record)
      (insert-record entity h2-record))))
