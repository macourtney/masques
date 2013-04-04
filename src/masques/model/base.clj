(ns masques.model.base
  (:use korma.core)
  (:require [clj-time.core :as clj-time]
            [clj-time.coerce :as clj-time-coerce]
            [clj-time.format :as clj-time-format]
            [clojure.string :as string]
            [clojure.tools.logging :as logging]
            [drift-db.core :as drift-db]
            [masques.core :as masques-core]
            [korma.db :as korma-db] )
  (:import [java.sql Clob]))

; Database connections. db is for non-korma stuff.
(def db (deref masques-core/db))
(korma-db/defdb mydb (drift-db/db-map))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Data sanitation: Clob helpers.
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

(defn clean-clob [record field-name field-data]
    (assoc record field-name (load-clob field-data)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Data sanitation: Date/time helpers.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def time-offset -5)

(defn h2-to-date-time [h2-date-time]
  (clj-time-coerce/to-date-time h2-date-time))

(defn clean-date-time-field [record field-name field-data]
  (conj record { field-name (h2-to-date-time field-data) })
  record)

(defn set-created-at [record]
  (conj record {:CREATED_AT (str (clj-time/now))}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Data sanitation: etc.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn as-boolean [value]
  (and value (not (= value 0))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Turn h2 keywords (:THAT_LOOK_LIKE_THIS) into clojure-style
; keywords (:that-look-like-this) and back again.
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

; Removes empty fields, fixes Clob data.
(defn clean-field-data [record field-name]
  (let [field-data (get record field-name)]
    (cond
      (nil? field-data)
        (remove-item field-name record)
      (instance? Clob field-data)
        (clean-clob record field-name field-data)
      (instance? java.sql.Timestamp field-data)
        (clean-date-time-field record field-name field-data)
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Entities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; album
(defn prepare-album [record]
  (set-created-at record))
(defn clean-up-album-for-clojure [record]
  (let [clean (clean-up-for-clojure record)]
    ; (println "Clean: " clean)
    ; (println "Dirty: " record)
    ; (println "Created at: " (:created-at clean))
    ; (println "Type: " (type (:created-at clean)))
    ; (println "Timestamp?: " (instance? java.sql.Timestamp (:created-at clean)))
    clean))

(defentity album
  (prepare prepare-album)
  (transform clean-up-album-for-clojure)
  (table :ALBUM))

; file
(defn prepare-file [record]
  (set-created-at record))
(defentity file
  (prepare prepare-file)
  (transform clean-up-for-clojure)
  (table :FILE))

; friend
(defn prepare-friend [record]
  record)
(defentity friend 
  (prepare prepare-friend)
  (transform clean-up-for-clojure)
  (table :FRIEND))

; log
(defn prepare-log [record]
  record)
(defentity log
  (prepare prepare-log)
  (transform clean-up-for-clojure)
  (table :LOG))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; SQL/ORM helpers.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn find-by-id [entity id]
  (clean-up-for-clojure (first (select entity (where {:ID id})))))

(defn insert-record [entity record]
  (println (str "before insert: " record))
  (let [id (vals (insert entity (values record)))]
    (println "the insert just happened")
    (println (str "after insert: " (find-by-id entity id)))
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Other randomness, used in models.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn remove-listener [listeners listener-to-remove]
  (remove #(= listener-to-remove %) listeners))
