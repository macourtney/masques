(ns masques.model.registry-editor
  (:refer-clojure :exclude [read-string])
  (:import [com.github.sarxos.winreg HKey RegistryException WindowsRegistry]))

(def hkey-current-user HKey/HKCU)
(def hkey-local-machine HKey/HKLM)

(defn read-string [^HKey hkey ^String key ^String value-name]
  (.readString (WindowsRegistry/getInstance) hkey key value-name))

(defn read-string-values [^HKey hkey ^String key]
  (.readStringValues (WindowsRegistry/getInstance) hkey key))

(defn read-string-sub-keys [^HKey hkey ^String key]
  (.readStringSubKeys (WindowsRegistry/getInstance) hkey key))

(defn create-key [^HKey hkey ^String key]
  (.createKey (WindowsRegistry/getInstance) hkey key))

(defn write-string-value [^HKey hkey ^String key ^String value-name ^String value]
  (.writeStringValue (WindowsRegistry/getInstance) hkey key value-name value))

(defn delete-key [^HKey hkey ^String key]
  (.deleteKey (WindowsRegistry/getInstance) hkey key))

(defn delete-value [^HKey hkey ^String key ^String value]
  (.deleteValue (WindowsRegistry/getInstance) hkey key value))
