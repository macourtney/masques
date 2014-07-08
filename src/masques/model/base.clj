(ns masques.model.base
  (:use korma.core)
  (:require [clj-time.core :as clj-time]
            [clj-time.coerce :as clj-time-coerce]
            [clojure.string :as string]
            [clojure.tools.logging :as logging]
            [drift-db.core :as drift-db]
            [masques.core :as masques-core]
            [korma.db :as korma-db] )
  (:import [java.sql Clob]
           [java.util.UUID]))

; The id key as a valid h2 keyword.
(def id-key :ID)

; The id key as a valid clojure keyword.
(def clojure-id :id)

; The created at key as a valid h2 keyword.
(def created-at :CREATED_AT)

; The created at key as a valid clojure keyword.
(def clojure-created-at :created-at)

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

(defn find-created-at
  "Returns the created at value from the given record."
  [record]
  (or (clojure-created-at record) (created-at record)))

(defn unset-created-at
  "Returns a new record with the created at key removed."
  [record]
  (if (clojure-created-at record)
    (dissoc record clojure-created-at)
    (dissoc record created-at)))

(defn set-created-at
  "Sets the created at value for the given record to the current date and time.
If the record contains a created at key, then it is assumed the created at key
is already set in the database and it is removed from the record."
  [record]
  (if (find-created-at record)
    (unset-created-at record)
    (assoc record created-at (str (clj-time/now)))))

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
      (instance? org.joda.time.DateTime (clojure-created-at record))
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
; interceptor helpers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; Called after a record has been inserted into the database.
(def insert-interceptors (atom {}))
; Called after a record has been updated in the database.
(def update-interceptors (atom {}))
; Called after a record has been deleted in the database.
(def delete-interceptors (atom {}))
; Called after a record has been changed in any way in the database.
(def change-interceptors (atom {}))

(defn insert-interceptors?
  "Returns true if there are insert interceptors registered."
  []
  (not-empty @insert-interceptors))

(defn update-interceptors?
  "Returns true if there are update interceptors registered."
  []
  (not-empty @update-interceptors))

(defn delete-interceptors?
  "Returns true if there are delete interceptors registered."
  []
  (not-empty @delete-interceptors))

(defn change-interceptors?
  "Returns true if there are change interceptors registered."
  []
  (not-empty @change-interceptors))

(defn interceptor-set-for-entity
  "Returns the interceptor set for the given entity in the given interceptors
atom."
  [interceptors-map entity]
  (or (get interceptors-map entity) #{}))

(defn add-interceptor-to-interceptors-map
  "Adds the given interceptor to the given interceptors map under the given
entity"
  [interceptors-map entity interceptor]
  (if (and entity interceptor)
    (assoc interceptors-map entity
           (conj (interceptor-set-for-entity interceptors-map entity)
                 interceptor))
    interceptors-map))

(defn remove-interceptor-from-interceptors-map
  "Removes the given interceptor from the given interceptors map under the given
entity"
  [interceptors-map entity interceptor]
  (let [interceptor-set
          (disj (interceptor-set-for-entity interceptors-map entity)
                interceptor)]
    (if (empty? interceptor-set)
      (dissoc interceptors-map entity)
      (assoc interceptors-map entity interceptor-set))))

(defn add-interceptor
  "Adds the given interceptor to the given interceptor atom as a interceptor for
the given entity."
  [interceptors-atom entity interceptor]
  (reset! interceptors-atom
          (add-interceptor-to-interceptors-map @interceptors-atom entity
                                               interceptor)))

(defn remove-interceptor
  "Removes the given interceptor from the given interceptors atom as a
interceptor for the given entity."
  [interceptors-atom entity interceptor]
  (reset! interceptors-atom
          (remove-interceptor-from-interceptors-map @interceptors-atom entity
                                                    interceptor)))

(defn all-interceptors
  "Returns all of the interceptors in the given atom for the given entity."
  [interceptors-atom entity]
  (interceptor-set-for-entity @interceptors-atom entity))

(defn create-interceptor-chain
  "Creates the chain of interceptors to notify."
  [action interceptors]
  (reduce #(partial %2 %1) action interceptors))

(defn call-interceptors
  "Generates an interceptor chain for the given interceptors and action then
calls the chain with the given record."
  [interceptors action record]
  ((create-interceptor-chain action interceptors) record))

(defn add-insert-interceptor
  "Adds the given interceptor to the list of insert interceptors for the given
entity."
  [entity interceptor]
  (add-interceptor insert-interceptors entity interceptor))

(defn remove-insert-interceptor
  "Removes the given interceptor from the list of insert interceptors for the
given entity."
  [entity interceptor]
  (remove-interceptor insert-interceptors entity interceptor))

(defn run-insert
  "Concates all of the insert and change interceptors for the given entity and
calls them with the given action and record."
  [entity action record]
  (call-interceptors
    (concat
      (all-interceptors insert-interceptors entity)
      (all-interceptors change-interceptors entity))
    action record))

(defn add-update-interceptor
  "Adds the given interceptor to the list of update interceptors for the given
entity."
  [entity interceptor]
  (add-interceptor update-interceptors entity interceptor))

(defn remove-update-interceptor
  "Removes the given interceptor from the list of update interceptors for the
given entity."
  [entity interceptor]
  (remove-interceptor update-interceptors entity interceptor))

(defn run-update
  "Concates all of the update and change interceptors for the given entity and
calls them with the given action and record."
  [entity action record]
  (call-interceptors
    (concat
      (all-interceptors update-interceptors entity)
      (all-interceptors change-interceptors entity))
    action record))

(defn add-delete-interceptor
  "Adds the given interceptor to the list of delete interceptors for the given
entity."
  [entity interceptor]
  (add-interceptor delete-interceptors entity interceptor))

(defn remove-delete-interceptor
  "Removes the given interceptor from the list of delete interceptors for the
given entity."
  [entity interceptor]
  (remove-interceptor delete-interceptors entity interceptor))

(defn run-delete
  "Concates all of the delete and change interceptors for the given entity and
calls them with the given action and record."
  [entity action record]
  (call-interceptors
    (concat
      (all-interceptors delete-interceptors entity)
      (all-interceptors change-interceptors entity))
    action record))

(defn add-change-interceptor
  "Adds the given interceptor to the list of change interceptors for the given
entity."
  [entity interceptor]
  (add-interceptor change-interceptors entity interceptor))

(defn remove-change-interceptor
  "Removes the given interceptor from the list of change interceptors for the
given entity."
  [entity interceptor]
  (remove-interceptor change-interceptors entity interceptor))

(defprotocol InterceptorProtocol
  "A protocol for registering interceptors."
  (interceptor-entity [this]
    "Returns the entity for this protocol.")
  
  (create-insert-interceptor [this]
    "Creates a new insert interceptor to be added to the interceptor chain. If
the protocol does not support this type of interceptor then return nil.")
  
  (create-update-interceptor [this]
    "Creates a new update interceptor to be added to the interceptor chain. If
the protocol does not support this type of interceptor then return nil.")
  
  (create-delete-interceptor [this]
    "Creates a new delete interceptor to be added to the interceptor chain. If
the protocol does not support this type of interceptor then return nil.")
  
  (create-change-interceptor [this]
    "Creates a new change interceptor to be added to the interceptor chain. If
the protocol does not support this type of interceptor then return nil."))

(defprotocol InterceptorManagerProtocol
  "An protocol for adding and inserting a set of interceptors."

  (add-interceptors [this]
    "Called to add all interceptors in this protocol to the interceptor chains")
  
  (remove-interceptors [this]
    "Called to remove all interceptors in this protocol to the interceptor
chains"))

(deftype InterceptorManager [interceptor-protocol insert-interceptor
                             update-interceptor delete-interceptor
                             change-interceptor]
  InterceptorManagerProtocol
  (add-interceptors [this]
    (when-let [current-entity (interceptor-entity interceptor-protocol)]
      (when-let [new-insert-interceptor
                 (create-insert-interceptor interceptor-protocol)]
        (add-insert-interceptor current-entity new-insert-interceptor)
        (reset! insert-interceptor new-insert-interceptor))
      (when-let [new-update-interceptor
                 (create-update-interceptor interceptor-protocol)]
        (add-update-interceptor current-entity new-update-interceptor)
        (reset! update-interceptor new-update-interceptor))
      (when-let [new-delete-interceptor
                 (create-delete-interceptor interceptor-protocol)]
        (add-delete-interceptor current-entity new-delete-interceptor)
        (reset! delete-interceptor new-delete-interceptor))
      (when-let [new-change-interceptor
                 (create-change-interceptor interceptor-protocol)]
        (add-change-interceptor current-entity new-change-interceptor)
        (reset! change-interceptor new-change-interceptor))))
  
  (remove-interceptors [this]
    (when-let [current-entity (interceptor-entity interceptor-protocol)]
      (when @insert-interceptor
        (remove-insert-interceptor current-entity @insert-interceptor)
        (reset! insert-interceptor nil))
      (when @update-interceptor
        (remove-update-interceptor current-entity @update-interceptor)
        (reset! update-interceptor nil))
      (when @delete-interceptor
        (remove-delete-interceptor current-entity @delete-interceptor)
        (reset! delete-interceptor nil))
      (when @change-interceptor
        (remove-change-interceptor current-entity @change-interceptor)
        (reset! change-interceptor nil)))))

(defn create-interceptor-manager
  "Creates a new interceptor manager with the given interceptor protocol and
adds the interceptors to the interceptor chains."
  [interceptor-protocol]
  (let [manager (InterceptorManager.
                  interceptor-protocol (atom nil) (atom nil) (atom nil)
                  (atom nil))]
    (add-interceptors manager)
    manager))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; SQL/ORM helpers.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn id
  "Returns the id of the given record or if the given record is not a map then
this function simply returns the record."
  [record]
  (cond
    (map? record) (or (id-key record) (clojure-id record))
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

(defn find-by-id
  "Returns the record for the given entity with the given id."
  [entity record-id]
  (when (and entity record-id)
    (clean-up-for-clojure
      (first (select entity (where { id-key (id record-id) }))))))

(defn create-insert-action
  "Creates an insert action which inserts a record into the database for the
given entity."
  [entity]
  (fn [record]
    (let [new-id-map (insert entity (values (clean-up-for-h2 record)))]
      (or (first (vals new-id-map)) (id record)))))

(defn insert-record
  "Inserts the given record into the database for the given entity"
  [entity record]
  (run-insert entity (create-insert-action entity) record))

(defn create-update-action
  "Creates an update action which updates a record in the database for the
given entity."
  [entity]
  (fn [record]
    (let [record-id (id record)]
      (update entity
        (set-fields (clean-up-for-h2 record))
        (where { id-key record-id }))
      record-id)))

(defn update-record
  "Updates the given record in the database for the given entity"
  [entity record]
  (run-update entity (create-update-action entity) record))

(defn insert-or-update
  "Inserts or updates the given record for the given entity. If the record has
an id, then this function performs an update. Otherwise, this function performs
an insert."
  [entity record]
  (when (and entity record)
    (if (id record)
      (update-record entity record)
      (insert-record entity record))))

(defn create-delete-action
  "Creates an delete action which deletes a record in the database for the
given entity."
  [entity]
  (fn [record]
    (let [record-id (id record)]
      (delete entity (where { id-key record-id }))
      record-id)))

(defn delete-record
  "Deletes the given record in the database for the given entity"
  [entity record]
  (when (id record)
    (run-delete entity (create-delete-action entity) record)))

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

(defn as-boolean
  "Converts the given int value as a boolean. Returns false if value is nil or
0."
  [value]
  (and value (not (= value 0))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Swing model support
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn index-of
  "Returns the index of the given record-id in the given list of records."
  [record-id records]
  (when-let [record-id (id record-id)]
    (some
      (fn [record-pair]
        (when (= (first record-pair) record-id)
          (second record-pair)))
      (map #(list %1 %2) (map id records) (range)))))