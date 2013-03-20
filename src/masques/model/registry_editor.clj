(ns masques.model.registry-editor
  (:require [clojure.string :as string]
            [masques.model.operating-system :as operating-system])
  (:import [java.util.prefs Preferences]))

(def hkey-current-user (inc Integer/MIN_VALUE))
(def hkey-local-machine (inc (inc Integer/MIN_VALUE)))

(def reg-success 0)
(def reg-not-found 2)
(def reg-access-denied 5)

(def key-all-access 0xf003f)
(def key-read 0x20019)
(def user-root (Preferences/userRoot))
(def system-root (Preferences/systemRoot))
(def user-class (class user-root))

(defn user-class-method [name & parameter-types]
  (doto (.getDeclaredMethod user-class name (into-array parameter-types))
    (.setAccessible true)))

(def byte-array-class (Class/forName "[B"))

(try

  (when (operating-system/windows?)
    (def reg-open-key (user-class-method "WindowsRegOpenKey" Integer/TYPE byte-array-class Integer/TYPE))

    (def reg-close-key (user-class-method "WindowsRegCloseKey" Integer/TYPE))

    (def reg-query-value-ex (user-class-method "WindowsRegQueryValueEx" Integer/TYPE byte-array-class))

    (def reg-enum-value (user-class-method "WindowsRegEnumValue" Integer/TYPE Integer/TYPE Integer/TYPE))

    (def reg-query-info-key (user-class-method "WindowsRegQueryInfoKey1" Integer/TYPE))

    (def reg-enum-key-ex (user-class-method "WindowsRegEnumKeyEx" Integer/TYPE Integer/TYPE Integer/TYPE))

    (def reg-create-key-ex (user-class-method "WindowsRegCreateKeyEx" Integer/TYPE byte-array-class))

    (def reg-set-value-ex (user-class-method "WindowsRegSetValueEx" Integer/TYPE byte-array-class byte-array-class))

    (def reg-delete-value (user-class-method "WindowsRegDeleteValue" Integer/TYPE byte-array-class))

    (def reg-delete-key (user-class-method "WindowsRegDeleteKey" Integer/TYPE byte-array-class)))

  (catch Exception exception
    (.printStackTrace exception)))

(defn to-c-str [^String string]
  (byte-array (map #(byte (Character/getNumericValue %)) (concat string [0]))))

(defn root [^Integer hkey]
  (condp = hkey
    hkey-local-machine system-root
    hkey-current-user user-root
    (IllegalArgumentException. (str "hkey=" hkey))))

(defn read-string
  ([^Integer hkey ^String key ^String value-name]
    (read-string (root hkey) hkey key value-name))

  ([^Preferences root ^Integer hkey ^String key ^String value-name]
    (let [handles (.invoke reg-open-key root (into-array [hkey (to-c-str key) key-read]))]
      (if (= reg-success (second handles))
        (let [result (.invoke reg-query-value-ex root (into-array [(first handles) (to-c-str value-name)]))]
          (.invoke reg-close-key root (into-array [(first handles)]))
          (when result
            (string/trim (String. result))))
        (second handles)))))

(defn read-string-values
  ([^Integer hkey ^String key]
    (read-string-values (root hkey) hkey key))

  ([^Preferences root ^Integer hkey ^String key]
    (let [handles (.invoke reg-open-key root (into-array [hkey (to-c-str key) key-read]))]
      (when (= reg-success (second handles))
        (let [info (.invoke reg-query-info-key root (into-array [(first handles)]))
              count (first info)
              max-length (nth info 3)
              results (reduce
                        (fn [results name]
                          (assoc results (string/trim name) (read-string hkey key name)))
                        {}
                        (map
                          (fn [index]
                            (String. (.invoke reg-enum-value root (into-array [(first handles) index (inc max-length)]))))
                          (range 0 count)))]
          (.invoke reg-close-key root (into-array [(first handles)]))
          results)))))

(defn read-string-sub-keys
  ([^Integer hkey ^String key]
    (read-string-sub-keys (root hkey) hkey key))

  ([^Preferences root ^Integer hkey ^String key]
    (let [handles (.invoke reg-open-key root (into-array [hkey (to-c-str key) key-read]))]
      (when (= reg-success (second handles))
        (let [info (.invoke reg-query-info-key root (into-array [(first handles)]))
              count (first info)
              max-length (nth info 3)
              results (map
                        (fn [index]
                          (string/trim (String. (.invoke reg-enum-key-ex root (into-array [(first handles) index (inc max-length)])))))
                        (range 0 count))]
          (.invoke reg-close-key root (into-array [(first handles)]))
          results)))))

(defn create-key
  ([^Integer hkey ^String key]
    (let [root (root hkey)
          return (create-key root hkey key)]
      (.invoke reg-close-key root (into-array [(first return)]))
      (when (not (= (second return) reg-success))
        (IllegalArgumentException. (str "rc=" (second return) "  key=" key)))))

  ([^Preferences root ^Integer hkey ^String key]
    (println "Parameters Types:" (seq (.getParameterTypes reg-create-key-ex)))
    (let [parameters (into-array Object [(int hkey) (to-c-str key)])]
      (println "Parameters:" (seq (map class parameters)))
      (.invoke reg-create-key-ex root parameters))))

(defn write-string-value
  ([^Integer hkey ^String key ^String value-name ^String value]
    (write-string-value (root hkey) hkey key value-name value))

  ([^Preferences root ^Integer hkey ^String key ^String value-name ^String value]
    (let [handles (.invoke reg-open-key root (into-array [hkey (to-c-str key) key-all-access]))]
      (.invoke reg-set-value-ex root (into-array [(first handles) (to-c-str value-name) (to-c-str value)]))
      (.invoke reg-close-key root (into-array [(first handles)])))))

(defn delete-key
  ([^Integer hkey ^String key]
    (let [result-code (delete-key (root hkey) hkey key)]
      (when (not (= reg-success result-code))
        (throw (IllegalArgumentException. (str "result-code = " result-code "  key = " key))))))

  ([^Preferences root ^Integer hkey ^String key]
    (.invoke reg-delete-key root (into-array [hkey (to-c-str key)]))))

(defn delete-value
  ([^Integer hkey ^String key ^String value]
    (let [result-code (delete-value (root hkey) hkey key value)]
      (when (not (= reg-success result-code))
        (throw (IllegalArgumentException. (str "result-code = " result-code "  key = " key "  value = " value))))))

  ([^Preferences root ^Integer hkey ^String key ^String value]
    (let [handles (.invoke reg-open-key root (into-array [hkey (to-c-str key) key-all-access]))]
      (if (= reg-success (second handles))
        (let [result-code (.invoke reg-delete-value root (into-array [(first handles) (to-c-str value)]))]
          (.invoke reg-close-key root (into-array [(first handles)]))
          result-code)
        (second handles)))))
