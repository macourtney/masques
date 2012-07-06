(ns masques.model.clipboard
  (:import [java.awt Toolkit]
           [java.awt.datatransfer StringSelection])
  (:require [seesaw.dnd :as dnd]))

(defn system []
  (.getSystemClipboard (Toolkit/getDefaultToolkit)))

(defn save-to-clipboard!
  "Copies the given text to the clipboard using the clipboard-owner."
  [text]
  (.setContents (system) (dnd/default-transferable [dnd/string-flavor text]) nil))

(defn retrieve-from-clipboard
  "Retrieve the current content of the system clipboard in the given flavor.
   If omitted, flavor defaults to seesaw.dnd/string-flavor. If not content
   with the given flavor is found, returns nil."
  ([] (retrieve-from-clipboard dnd/string-flavor))
  ([flavor]
    (try
      (.getData (system) (dnd/to-raw-flavor flavor))
      (catch java.awt.datatransfer.UnsupportedFlavorException e nil))))