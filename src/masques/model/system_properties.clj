(ns masques.model.system-properties
  (:require [masques.model.operating-system :as operating-system]
            [masques.model.registry-editor :as registry-editor])
  (:import [com.github.sarxos.winreg RegistryException]))

(def masques-win-system-key "SOFTWARE\\Masques")
(def masques-win-data-directory "datadir")
(def masques-win-hkey registry-editor/hkey-current-user)

(defn set-data-directory [^String data-directory]
  (when (operating-system/windows?)
    (registry-editor/create-key masques-win-hkey masques-win-system-key)
    (registry-editor/write-string-value masques-win-hkey masques-win-system-key masques-win-data-directory
                                        data-directory)))

(defn read-data-directory []
  (when (operating-system/windows?)
    (try
      (registry-editor/read-string masques-win-hkey masques-win-system-key masques-win-data-directory)
      (catch RegistryException registry-exception
        nil))))

(defn delete-data-directory []
  (when (operating-system/windows?)
    (registry-editor/delete-key masques-win-hkey masques-win-system-key)))