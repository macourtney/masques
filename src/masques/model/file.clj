(ns masques.model.file
  (:use masques.model.base
        korma.core))

(defn save [record]
  (insert-or-update file record))

(defn by-album [album-id]
  (into [] (select file (where {:ALBUM_ID album-id}))))