;; This file is used to configure the database and connection.

(ns config.db-config
  (:require [clj-i2p.core :as clj-i2p]
            [clojure.string :as string]
            [clojure.tools.logging :as logging]
            [config.environment :as environment]
            [drift-db-h2.flavor :as h2])
  (:import [java.io File]))

(def data-directory (atom "data/db/"))

(def username (atom nil))

(def password (atom nil))

(def users-map (atom {}))

(defn user-directory-taken? [username user-directory]
  (contains? (set (vals (dissoc @users-map username))) user-directory))

(defn user-directory [username]
  (let [user-dir (string/replace username #"[ \\/\?%:\|\"<>\t\n\r\f\a\e]" "_")]
    (if (not (user-directory-taken? username user-dir))
      user-dir
      (some #(when (not (user-directory-taken? username %1)) %1) (map #(str user-dir %) (range))))))

(defn add-username [username]
  (swap! users-map assoc username (user-directory username)))

(defn add-username-if-missing [username]
  (when-not (contains? @users-map username)
    (add-username username)))

(defn get-users-map []
  @users-map)

(defn reset-users-map
  ([] (reset-users-map {}))
  ([new-users-map]
    (reset! users-map new-users-map)))

(defn data-dir []
  @data-directory)

(defn user-data-directory []
  (when-let [data-dir (data-dir)]
    (when-let [user-directory (get @users-map @username)]
      (str data-dir user-directory "/"))))

(defn update-private-key-directory []
  (when-let [private-key-directory (user-data-directory)]
    (clj-i2p/set-private-key-file-directory (File. private-key-directory))))

(defn update-data-directory [new-data-dir]
  (reset! data-directory new-data-dir)
  (update-private-key-directory))

(defn update-username-password [new-username new-password]
  (reset! username new-username)
  (add-username-if-missing new-username)
  (reset! password new-password)
  (update-private-key-directory))

(defn dbname [environment]
  (condp = environment
     ;; The name of the production database to use.
     :production "masques_production"

     ;; The name of the development database to use.
     :development "masques_development"

     ;; The name of the test database to use.
     :test "masques_test"))

(defn
#^{:doc "Returns the database flavor which is used by Conjure to connect to the database."}
  create-flavor [environment]
  (logging/info (str "Environment: " environment))
  (h2/h2-flavor

    ;; Calculates the database to use.
    (dbname environment)

    (user-data-directory)

    @username

    @password

    ;; Include encryption only when both the username and password are present
    (and @username @password)))

(defn
  load-config []
  (let [environment (environment/environment-name)]
    (if-let [flavor (create-flavor (keyword environment))]
      flavor
      (throw (new RuntimeException (str "Unknown environment: " environment ". Please check your conjure.environment system property."))))))
