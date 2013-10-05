(ns masques.model.avatar
  (:require [clojure.java.io :as io]
            [config.db-config :as db-config]
            [image-resizer.core :as resizer]
            [image-resizer.format :as format]
            [masques.model.file :as file-model])
  (:use masques.model.base
        korma.core))

(def avatar-width 150)

(defn resize-avatar [source-path]
  (format/as-file (resizer/resize-to-width (io/file source-path) avatar-width) (str (db-config/user-data-directory) "avatar.png")))

(defn create-avatar-image [source-path]
  ; (try
    (resize-avatar source-path))
    ; (catch Exception e (str "Caught exception: " (.getMessage e)))))

