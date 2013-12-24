;; This file is used to configure the database and connection.

(ns config.db-config
  (:require [clj-i2p.core :as clj-i2p]
            [clojure.java.io :as java-io]
            [clojure.string :as string]
            [clojure.tools.logging :as logging]
            [clojure.tools.string-utils :as string-utils]
            [config.environment :as environment]
            [drift-db-h2.flavor :as h2]
            [masques.edn :as edn]))

(def user-filename-str "username.clj")

(def data-directory (atom "data/db/"))

(def username (atom nil))

(def password (atom nil))

(def users-map (atom nil))

(def user-add-listeners (atom []))

(defn add-user-add-listener [user-add-listener]
  (swap! user-add-listeners conj user-add-listener))
  
(defn call-user-add-listeners [username]
  (doseq [user-add-listener @user-add-listeners]
    (user-add-listener username)))

(defn current-username []
  @username)

(defn data-dir []
  @data-directory)

(defn username-from-directory [user-directory]
  (when-let [username-file (java-io/file user-directory user-filename-str)]
    (when (.exists username-file)
      (edn/read username-file))))

(defn add-user-directory-to-map [users-map user-directory]
  (if user-directory
    (assoc users-map (username-from-directory user-directory) (.getName user-directory))
    users-map))

(defn load-users-map []
  (when-let [data-dir (data-dir)]
    (let [data-dir-file (java-io/file data-dir)]
      (reset! users-map
              (reduce add-user-directory-to-map
                      {}
                      (filter #(.isDirectory %) (.listFiles data-dir-file)))))))

(defn ensure-users-map []
  (when (nil? @users-map)
    (load-users-map))
  @users-map)

(defn all-users []
  (keys (ensure-users-map)))
  
(defn find-user-directory
  ([] (find-user-directory @username))
  ([username] (get (ensure-users-map) username)))

(defn user-exists? [username]
  (contains? (ensure-users-map) username))
  
(defn validate-username [username]
  (when (and username (not-empty username) (not (user-exists? username)))
    username))

(defn user-directory-taken? [username user-directory]
  (contains? (set (vals (dissoc (ensure-users-map) username))) user-directory))

(defn user-directory [username]
  (let [user-dir (string/replace username #"[ \\/\?%:\|\"<>\t\n\r\f\a\e]" "_")] ;"
    (if (not (user-directory-taken? username user-dir))
      user-dir
      (some #(when (not (user-directory-taken? username %1)) %1) (map #(str user-dir %) (range))))))

(defn add-username [username]
  (ensure-users-map)
  (when username
    (swap! users-map assoc username (user-directory username))))

(defn reset-users-map
  ([] (reset-users-map nil))
  ([new-users-map]
    (reset! users-map new-users-map)))

(defn user-data-directory
  ([] (user-data-directory @username))
  ([username]
    (when-let [data-dir (data-dir)]
      (when-let [user-directory (find-user-directory username)]
        (str (string-utils/add-ending-if-absent data-dir "/")
             (string-utils/add-ending-if-absent user-directory "/"))))))

(defn username-file
  ([] (username-file @username))
  ([username]
    (when-let [user-data-dir (user-data-directory username)]
      (str user-data-dir user-filename-str))))

(defn add-username-if-missing [username]
  (when (and username (not (user-exists? username)))
    (add-username username)
    (let [username-clj-file (java-io/file (username-file username))]
      (when-not (.exists username-clj-file)
        (.mkdirs (.getParentFile username-clj-file))
        (edn/write username-clj-file username)))
    (call-user-add-listeners username))
  username)

(defn update-private-key-directory []
  (when-let [private-key-directory (user-data-directory)]
    (clj-i2p/set-private-key-file-directory (java-io/file private-key-directory))))

(defn update-data-directory [new-data-dir]
  (reset! data-directory new-data-dir)
  (update-private-key-directory)
  (reset-users-map))

(defn as-string
  "Converts the given value to it's string value."
  [value]
  (cond
    (string? value) value
    ; char array.
    (instance? (Class/forName "[C") value) (String. value)
    (nil? value) value
    :else (throw (RuntimeException. (str "Unknown type:" (class value))))))

(defn update-username-password [new-username new-password]
  (let [new-password-str (as-string new-password)]
    (reset! username new-username)
    (add-username-if-missing new-username)
    (reset! password new-password-str)
    (update-private-key-directory)))

(defn dbname [environment]
  (condp = environment
     ;; The name of the production database to use.
     :production "masques_production"

     ;; The name of the development database to use.
     :development "masques_development"

     ;; The name of the test database to use.
     :test "masques_test"))

(defn create-flavor
  "Returns the database flavor which is used by Conjure to connect to the database."
  [environment]
  (logging/info (str "Environment: " environment))
  (h2/h2-flavor

    ;; Calculates the database to use.
    (dbname environment)

    (user-data-directory)

    @username

    @password

    ;; Include encryption only when both the username and password are present
    (and @username @password)))

(defn load-config
  "Loads the db flavor using the preset username, password and environment."
  []
  (let [environment (environment/environment-name)]
    (if-let [flavor (create-flavor (keyword environment))]
      flavor
      (throw (new RuntimeException (str "Unknown environment: " environment ". Please check your conjure.environment system property."))))))

(defn create-user
  "Creates the given user with the given password."
  [new-username new-password]
  (when-let [new-username (add-username-if-missing new-username)]
    (let [old-username @username
          old-password @password]
      (try
        (update-username-password new-username new-password)
        (load-config)
        new-username
        (finally
          (update-username-password old-username old-password))))))