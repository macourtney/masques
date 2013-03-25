(ns masques.model.system-properties
  (:require [masques.model.operating-system :as operating-system]
            [masques.model.registry-editor :as registry-editor])
  (:import [com.github.sarxos.winreg RegistryException]
           [java.io File FileInputStream FileOutputStream]
           [java.util Properties]))

(def masques-win-system-key "SOFTWARE\\Masques")
(def masques-win-data-directory "datadir")
(def masques-win-hkey registry-editor/hkey-current-user)

(def masques-unix-data-directory "datadir")

(defn win-set-data-directory [^String data-directory]
  (registry-editor/create-key masques-win-hkey masques-win-system-key)
  (registry-editor/write-string-value masques-win-hkey masques-win-system-key masques-win-data-directory
                                      data-directory))

(defn unix-user-directory []
  (File. (System/getProperty "user.home")))

(defn unix-masques-directory []
  (File. (unix-user-directory) ".masques"))

(defn unix-ensure-masques-directory []
  (let [masques-directory (unix-masques-directory)]
    (when (not (.exists masques-directory))
      (.mkdirs masques-directory))
    masques-directory))

(defn unix-config-properties []
  (File. (unix-ensure-masques-directory) "masques.properties"))

(defn masques-properties []
  (let [config-properties (unix-config-properties)]
    (if (.exists config-properties)
      (with-open [properties-file-stream (FileInputStream. config-properties)]
        (doto (new Properties)
          (.load properties-file-stream)))
      (new Properties))))

(defn save-masques-properties [^Properties masques-properties]
  (when masques-properties
    (with-open [properties-file-stream (FileOutputStream. (unix-config-properties))]
      (.store masques-properties properties-file-stream))))

(defn unix-set-data-directory [^String data-directory]
  (let [properties-file (masques-properties)]
    (.setProperty properties-file masques-unix-data-directory data-directory)
    (save-masques-properties properties-file)))

(defn unix-read-data-directory []
  (.getProperty (masques-properties) masques-unix-data-directory))

(defn win-read-data-directory []
  (try
    (registry-editor/read-string masques-win-hkey masques-win-system-key masques-win-data-directory)
    (catch RegistryException registry-exception
      nil)))

(defn unix-delete-data-directory []
  (let [properties-file (masques-properties)]
    (.setProperty properties-file masques-unix-data-directory nil)
    (save-masques-properties properties-file)))

(defn win-delete-data-directory []
  (registry-editor/delete-key masques-win-hkey masques-win-system-key))

(defn set-data-directory [^String data-directory]
  (if (operating-system/windows?)
    (win-set-data-directory data-directory)
    (unix-set-data-directory data-directory)))

(defn read-data-directory []
  (if (operating-system/windows?)
    (win-read-data-directory)
    (unix-read-data-directory)))

(defn delete-data-directory []
  (if (operating-system/windows?)
    (win-delete-data-directory)
    (unix-delete-data-directory)))