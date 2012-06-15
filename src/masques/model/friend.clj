(ns masques.model.friend
  (:require [clj-i2p.core :as clj-i2p]
            [clj-i2p.peer-service.peer :as clj-i2p-peer]
            [clj-record.boot :as clj-record-boot]
            [clojure.data.xml :as data-xml]
            [clojure.java.io :as io]
            [masques.model.identity :as identity]
            [masques.model.user :as user])
  (:use masques.model.base)
  (:import [java.io FileInputStream FileOutputStream OutputStreamWriter]))

(def friend-add-listeners (atom []))

(def friend-delete-listeners (atom []))

(defn add-friend-add-listener [listener]
  (swap! friend-add-listeners conj listener))

(defn remove-friend-add-listener [listener]
  (swap! friend-add-listeners remove-listener listener))

(defn friend-add [friend]
  (doseq [listener @friend-add-listeners]
    (listener friend)))

(defn friend-add-listener-count []
  (count @friend-add-listeners))

(defn add-friend-delete-listener [listener]
  (swap! friend-delete-listeners conj listener))

(defn remove-friend-delete-listener [listener]
  (swap! friend-delete-listeners remove-listener listener))

(defn friend-delete [friend]
  (doseq [listener @friend-delete-listeners]
    (listener friend)))

(defn friend-delete-listener-count []
  (count @friend-delete-listeners))

(clj-record.core/init-model
  (:associations (belongs-to identity)
                 (belongs-to friend :fk friend_id :model identity))
  (:callbacks (:after-insert friend-add)
              (:after-destroy friend-delete)))

(defn all-friends
  "Returns all of the friends for the current identity"
  ([] (all-friends (identity/current-user-identity)))
  ([identity]
    (find-records { :identity_id (:id identity) })))

(defn friend?
  "Returns true if the given friend is a friend of the given or current identity."
  ([friend-identity] (friend? friend-identity (identity/current-user-identity)))
  ([friend-identity identity]
    (when-let [identity-id (:id identity)]
      (when-let [friend-id (:id friend-identity)]
        (find-record { :identity_id identity-id :friend_id friend-id })))))

(defn add-friend
  "Removes the given friend for the given or current identity."
  ([friend-identity] (add-friend friend-identity (identity/current-user-identity)))
  ([friend-identity identity]
    (when (not (friend? friend-identity identity))
      (when-let [identity-id (:id identity)]
        (when-let [friend-id (:id friend-identity)]
          (insert { :identity_id identity-id :friend_id friend-id }))))))

(defn remove-friend
  "Removes the given friend for the given or current identity."
  ([friend-identity] (remove-friend friend-identity (identity/current-user-identity)))
  ([friend-identity identity]
    (when-let [friend-to-remove (friend? friend-identity identity)]
      (destroy-record friend-to-remove))))

(defn friend-xml
  "Returns the xml needed to add the logged in user as a friend to another peer."
  ([] (friend-xml (user/current-user) (clj-i2p/base-64-destination)))
  ([user destination]
    (when destination
      (when-let [user-xml (user/xml user)]
        (data-xml/element :friend {}
          user-xml
          (data-xml/element :destination {} (clj-i2p/as-destination-str destination)))))))

(defn friend-xml-string
  ([] (friend-xml-string (user/current-user) (clj-i2p/base-64-destination)))
  ([user destination]
    (when-let [xml-element (friend-xml user destination)]
      (data-xml/indent-str xml-element))))

(defn write-friend-xml
  "Writes the friend xml to the given file. File can be either a java File class or a string."
  ([file] (write-friend-xml file (user/current-user) (clj-i2p/base-64-destination)))
  ([file user destination]
    (when-let [output-xml (friend-xml user destination)]
      (when-let [java-file (io/as-file file)]
        (with-open [output-stream (FileOutputStream. java-file)]
          (with-open [output (OutputStreamWriter. output-stream "UTF-8")]
            (data-xml/emit output-xml output)))))))

(defn parse-destination-xml
  "Parses the given xml element as a destination element. If the given xml element is not a valid destination element,
this function returns nil."
  [xml-element]
  (when (= (:tag xml-element) :destination)
    (first (:content xml-element))))

(defn load-friend-xml
  "Reads the given xml element and loads the data in to the database."
  ([xml-element] (load-friend-xml xml-element (identity/current-user-identity)))
  ([xml-element identity]
    (when (= (:tag xml-element) :friend)
      (when-let [xml-content (:content xml-element)]
        (when-let [user (some user/parse-xml xml-content)]
          (when-let [destination (some parse-destination-xml xml-content)]
            (clj-i2p-peer/add-peer-destination-if-missing destination)
            (when-let [friend-id (identity/add-or-update-identity user destination)]
              (add-friend { :id friend-id } identity))))))))

(defn read-friend-xml
  ([file] (read-friend-xml file (identity/current-user-identity)))
  ([file identity]
    (when identity
      (when-let [java-file (io/as-file file)]
        (when (.exists java-file)
          (with-open [file-input (FileInputStream. java-file)]
            (when-let [xml-element (data-xml/parse file-input)]
              (load-friend-xml xml-element identity))))))))

(defn read-friend-xml-string
  ([xml-string] (read-friend-xml-string xml-string (identity/current-user-identity)))
  ([xml-string identity]
    (when (and xml-string identity)
      (when-let [xml-element (data-xml/parse-str xml-string)]
        (load-friend-xml xml-element identity)))))