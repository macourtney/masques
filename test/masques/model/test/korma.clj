(ns masques.model.test.korma
  (:refer-clojure :exclude [comment group])
  (:require test.init)
  (:require [drift-db.core :as drift-db]
            [korma.db :as korma-db]
            [masques.model.base :as model]
            [masques.model.file :as file-model]
            [masques.model.album :as album-model]
            [masques.model.share :as share-model]
            [masques.model.message :as message-model]
            [masques.model.log :as log])
  (:use clojure.test
        korma.core))

(def database-map (drift-db/db-map))

(korma-db/defdb mydb database-map)

; (defentity album (table :ALBUM))
; (defentity file (table :FILE))
; (defentity friend (table :FRIEND))
(defentity grouping (table :GROUPING))
(defentity grouping-profile (table :GROUPING_PROFILE))
; (defentity log (table :LOG))
; (defentity message (table :MESSAGE))
(defentity profile (table :PROFILE))
(defentity property (table :PROPERTY))
; (defentity share (table :SHARE))
(defentity user (table :USER))

(defn h2-data-for-test-records []
  [; [album :NAME]
   ; [file :NAME]
   ; [friend :CREATED_AT]
   [grouping :NAME]
   [grouping-profile :CREATED_AT]
   ; [log :CREATED_AT]
   ; [message :CREATED_AT]
   [profile :ALIAS]
   [property :NAME]
   ; [share :CREATED_AT]
   [user :NAME]])

(defn h2-insert [entity data]
	(vals (insert entity (values data))))

(defn h2-find [entity id]
	(select entity (where {:ID id})))

(defn h2-all-tables []
  (exec-raw "SHOW TABLES" :results))

(defn h2-table-name [table-map]
  (:TABLE_NAME table-map))

(defn h2-table-names []
  (map h2-table-name (h2-all-tables)))

(defn h2-columns [table-name]
  (exec-raw (str "SHOW COLUMNS FROM " table-name) :results))

(defn h2-test-record [entity field]
  (let [id (h2-insert entity {field nil})
        schema (h2-find entity id)]
    (println (str "\n" (:table entity) ": " (first schema)))))

(defn h2-test-records []
  (doseq [table (h2-data-for-test-records)]
    (h2-test-record (first table) (second table))))

(defn h2-schema []
  (doseq [table (h2-table-names)]
    (println (str "\n===== " table " =====\n" (h2-columns table)))))

(deftest test-schema
  ; (println (file-model/save {:NAME "this file" :ALBUM_ID 1}))
  ; (println (file-model/save {:NAME "that file" :ALBUM_ID 1}))
  ; (println (album-model/save {:NAME "my album"}))
  ; (println (album-model/with-files 1))
  ; #_(h2-test-records) 
  (println (h2-schema))
  (println (model/set-uuid {:something "some value"}))

  (let [share-record { :message-id 42 } ]
    (share-model/save share-record))

  ; #_(println (h2-table-names))
  ; #_(println model/album)
)

(deftest test-find-all
  (let [test-message (message-model/find-message
                       (message-model/create-message "test message"))
        all-messages (model/find-all model/message
                                     { :id (model/id test-message) })]
    (is all-messages)
    (is (= all-messages [test-message]))
    (message-model/delete-message test-message)))
