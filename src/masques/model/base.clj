(ns masques.model.base
  (:use korma.core)
  (:require [clj-time.core :as clj-time]
            [clj-time.coerce :as clj-time-coerce]
            [clojure.string :as string]
            [clojure.tools.logging :as logging]
            [drift-db.core :as drift-db]
            [masques.core :as masques-core]
            [korma.db :as korma-db] )
  (:import [java.sql Clob])
  (:import [java.util.UUID]))

; The id key as a valid h2 keyword.
(def id-key :ID)

; Database connections. db is for non-korma stuff.
(def db (deref masques-core/db))
(korma-db/defdb mydb (drift-db/db-map))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Map helpers.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn remove-item [item record]
  (dissoc (into {} record) item))

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

(defn get-clob [record clob-key]
  (when-let [clob (clob-key record)]
    (when (instance? Clob clob)
      clob)))

(defn clean-clob-key [record clob-key]
  (if-let [clob (get-clob record clob-key)]
    (assoc record clob-key (clob-string clob))
    record))

(defn clean-clob-for-clojure [record field-name field-data]
  (assoc record field-name (clob-string field-data)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Data sanitation: Date/time helpers.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn h2-to-clojure-date-time [h2-date-time]
  (clj-time-coerce/to-date-time h2-date-time))

(defn clojure-to-h2-date-time [clojure-date-time]
  (clj-time-coerce/to-sql-date clojure-date-time))

(defn clean-date-time-for-clojure [record field-name field-data]
  (conj record { field-name (h2-to-clojure-date-time field-data) }))

(defn clean-date-time-for-h2 [record field-name field-data]
  (conj record { field-name (clojure-to-h2-date-time field-data) }))

(defn created-at-is-set [record]
  (or (:created-at record) (:CREATED_AT record)))

(defn unset-created-at [record]
  (if (:created-at record) (dissoc record :created-at) (dissoc record :CREATED_AT)))

(defn set-created-at [record]
  (if (created-at-is-set record)
    (unset-created-at record)
    (conj record {:CREATED_AT (str (clj-time/now))})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; UUID helpers.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn uuid [] 
  (str (java.util.UUID/randomUUID)))

(defn uuid-is-set [record]
  (or (:uuid record) (:UUID record)))

(defn unset-uuid [record]
  (if (:uuid record)
    (dissoc record :uuid)
    (dissoc record :UUID)))

(defn set-uuid [record]
  (if (uuid-is-set record)
    (unset-uuid record)
    (conj record {:UUID (uuid)})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Turn h2 keywords (:THAT_LOOK_LIKE_THIS) into clojure-style
; keywords (:that-look-like-this) and back again.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn lower-case [the-string]
  (string/lower-case the-string))

(defn upper-case [the-string]
  (when the-string
    (string/upper-case the-string)))

(defn remove-colon [the-string]
  (string/replace the-string ":" ""))

(defn replace-underscores-with-hyphens [has-underscores]
  (string/replace has-underscores "_" "-"))

(defn replace-hyphens-with-underscores [has-hyphens]
  (string/replace has-hyphens "-" "_"))

(defn clojure-keyword [h2-keyword]
  (keyword (lower-case (replace-underscores-with-hyphens (name h2-keyword)))))

(defn h2-keyword [clojure-keyword]
  (keyword (upper-case (replace-hyphens-with-underscores (name clojure-keyword)))))

(defn clean-field-data [record field-name]
  "Prepares H2 data, from the database, for the rest of our clojure application."
  (let [field-data (get record field-name)]
    (cond
      (instance? Clob field-data)
        (clean-clob-for-clojure record field-name field-data)
      (instance? java.sql.Timestamp field-data)
        (clean-date-time-for-clojure record field-name field-data)
      :else record)))

(defn clean-field-data-for-h2 [record field-name]
  "Prepares clojure data, from the application, for the database."
  (let [field-data (get field-name record)]
    (cond
      (instance? org.joda.time.DateTime (:created-at record))
        (clean-date-time-for-h2 record field-name field-data)
      :else record)))

(defn clojure-field-name [record field-name]
  (assoc (remove-item field-name record) (clojure-keyword field-name) (get record field-name)))

(defn h2-field-name [record field-name]
  (assoc (remove-item field-name record) (h2-keyword field-name) (get record field-name)))

(defn clean-up-for-clojure [record]
  (let [clean-data (reduce clean-field-data record (keys record))]
    (reduce clojure-field-name clean-data (keys clean-data))))

(defn clean-up-for-h2 [record]
  (let [clean-data (reduce clean-field-data record (keys record))]
    (reduce h2-field-name clean-data (keys clean-data))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; SQL/ORM helpers.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn id
  "Returns the id of the given record or if the given record is not a map then
this function simply returns the record."
  [record]
  (cond
    (map? record) (or (:ID record) (:id record))
    (integer? record) record
    (nil? record) nil
    :else (throw (RuntimeException. (str "Don't know how to get an id from a "
                                         (class record))))))

(defn find-all
  "Finds records which statisfy the given prototype."
  [entity record]
  (when (and entity record)
    (map clean-up-for-clojure
      (select entity (where (clean-up-for-h2 record))))))

(defn find-first
  "Finds the first record which statisfy the given prototype."
  [entity record]
  (when (and entity record)
    (first
      (map clean-up-for-clojure
        (select entity
          (where (clean-up-for-h2 record))
          (limit 1))))))

(defn find-by-id [entity id]
  (when id
    (clean-up-for-clojure (first (select entity (where {:ID id}))))))

(defn insert-record [entity record]
  (let [new-id-map (insert entity (values (clean-up-for-h2 record)))
        id (or (first (vals new-id-map)) (id record))]
    (find-by-id entity id)))

(defn update-record [entity record]
  (update entity
    (set-fields (clean-up-for-h2 record))
    (where {:ID (id record)}))
  (find-by-id entity (id record)))

(defn insert-or-update [entity record]
  (if (id record)
    (update-record entity record)
    (insert-record entity record)))

(defn delete-record [entity record]
  (when-let [id (if (map? record) (id record) record)]
    (delete entity
      (where { :ID id }))))

(defn count-records
  "Returns the number of records for the given entity. You can pass a record to
use as a prototype of the records to count."
  ([entity]
    (:count
      (first (select entity (aggregate (count :*) :count)))))
  ([entity record]
    (:count
      (first
        (select
          entity
          (aggregate (count :*) :count)
          (where record))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Entities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; ALBUM
(defn prepare-album-for-h2 [record]
  (set-created-at record))
(defn transform-album-for-clojure [record]
  (let [clean (clean-up-for-clojure record)]
    clean))
(defentity album
  (prepare prepare-album-for-h2)
  (transform transform-album-for-clojure)
  (table (h2-keyword :album)))

; FILE
(defn prepare-file-for-h2 [record]
  (set-created-at record))
(defentity file
  (prepare prepare-file-for-h2)
  (transform clean-up-for-clojure)
  (table (h2-keyword :file)))

; FRIEND_REQUEST
(defn prepare-friend-request-for-h2 [record]
  (set-created-at record))
(defentity friend-request 
  (prepare prepare-friend-request-for-h2)
  (transform clean-up-for-clojure)
  (table (h2-keyword :friend-request)))

; GROUPING
(defn prepare-grouping-for-h2 [record]
  (set-created-at record))
(defentity grouping 
  (transform clean-up-for-clojure)
  (prepare prepare-grouping-for-h2)
  (table (h2-keyword :grouping)))

; GROUPING_PROFILE
(defn prepare-grouping-profile-for-h2 [record]
  (set-created-at record))
(defentity grouping-profile
  (transform clean-up-for-clojure)
  (prepare prepare-grouping-profile-for-h2)
  (table (h2-keyword :grouping-profile)))

; LOG
(defn prepare-log-for-h2 [record]
  record)
(defentity log
  (prepare prepare-log-for-h2)
  (transform clean-up-for-clojure)
  (table (h2-keyword :log)))

; MESSAGE
(defn prepare-message-for-h2 [record]
  (set-created-at record))
(defentity message
  (prepare prepare-message-for-h2)
  (transform clean-up-for-clojure)
  (table (h2-keyword :message)))

; PROFILE
(defn prepare-profile-for-h2 [record]
  (set-created-at record))
(defentity profile
  (transform clean-up-for-clojure)
  (prepare prepare-profile-for-h2)
  (table (h2-keyword :profile)))

; SHARE
(defn prepare-share-for-h2 [record]
  (set-uuid (set-created-at record)))
(defentity share
  (transform clean-up-for-clojure)
  (prepare prepare-share-for-h2)
  (table (h2-keyword :share)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Other randomness, used in models.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn remove-listener [listeners listener-to-remove]
  (remove #(= listener-to-remove %) listeners))

(defn as-boolean [value]
  (and value (not (= value 0))))

