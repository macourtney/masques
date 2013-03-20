(ns masques.model.system-properties
  (:require [masques.model.operating-system :as operating-system]
            [masques.model.registry-editor :as registry-editor]))

(def masques-win-system-key "SOFTWARE/masques/")
(def masques-win-data-directory "datadir")

(defn set-data-directory [^String data-directory]
  (when (operating-system/windows?)
    (registry-editor/create-key registry-editor/hkey-local-machine masques-win-system-key)
    (registry-editor/write-string-value registry-editor/hkey-local-machine masques-win-system-key
                                        masques-win-data-directory data-directory)))

(defn read-data-directory []
  (when (operating-system/windows?)
    (registry-editor/read-string registry-editor/hkey-local-machine masques-win-system-key masques-win-data-directory)))

(defn delete-data-directory []
  (when (operating-system/windows?)
    (registry-editor/delete-key registry-editor/hkey-local-machine masques-win-system-key)))