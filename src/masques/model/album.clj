(ns masques.model.album
  (:use masques.model.base
        korma.core))

(defn find-album
  "Returns the album with the given id."
  [album-id]
  (when album-id
    (find-by-id album (id album-id))))

(defn save [record]
  (insert-or-update album record))

(defn get-files [album-id]
  (into [] (select file (where {:ALBUM_ID album-id}))))

(defn attach-files [album-record file-records]
  (assoc album-record :files file-records))

(defn with-files [id]
  (let [album-record (find-by-id album id)]
	(attach-files album-record (get-files id))))