(ns masques.controller.friend.add
  (:require [clojure.java.io :as java-io]
            [masques.controller.actions.utils :as actions-utils]
            [masques.controller.utils :as controller-utils]
            [masques.model.clipboard :as clipboard-model]
            [masques.model.friend :as friend-model]
            [masques.view.friend.add :as add-friend-view]
            [seesaw.core :as seesaw-core])
  (:import [java.io StringWriter]))

(defn read-file [file]
  (let [string-writer (new StringWriter)]
    (java-io/copy file string-writer)
    (when-let [friend-xml (.toString string-writer)]
      (when (not-empty friend-xml)
        friend-xml))))

(defn import-file [add-friend-panel file]
  (when file
    (when-let [friend-xml (read-file file)]
      (add-friend-view/friend-text add-friend-panel friend-xml))))

(defn import-action [add-friend-panel e]
  (when-let [file (controller-utils/choose-file add-friend-panel)]
    (import-file add-friend-panel file)))

(defn attach-import-action [add-friend-panel]
  (actions-utils/attach-frame-listener add-friend-panel "#import-button" import-action))

(defn paste-action [add-friend-panel e]
  (when-let [clipboard-text (clipboard-model/retrieve-from-clipboard)]
    (when (not-empty clipboard-text)
      (add-friend-view/friend-text add-friend-panel clipboard-text))))

(defn attach-paste-action [add-friend-panel]
  (actions-utils/attach-frame-listener add-friend-panel "#paste-button" paste-action))

(defn attach-cancel-action [add-friend-panel]
  (actions-utils/attach-window-close-listener add-friend-panel "#cancel-button"))

(defn add-action [add-friend-panel e]
  (controller-utils/disable-widget add-friend-panel)
  (if-let [friend-text-str (add-friend-view/friend-text add-friend-panel)]
    (do
      (friend-model/read-friend-xml-string friend-text-str)
      (actions-utils/close-window add-friend-panel))
    (controller-utils/enable-widget add-friend-panel)))

(defn attach-add-action [add-friend-panel]
  (actions-utils/attach-frame-listener add-friend-panel "#add-button" add-action))

(defn attach [add-friend-panel]
  (attach-import-action (attach-paste-action (attach-add-action (attach-cancel-action add-friend-panel)))))

(defn show [main-frame]
  (controller-utils/show (attach (add-friend-view/create main-frame))))