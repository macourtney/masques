(ns masques.test.util
  (:require [clojure.test :as clojure-test]
            [fixtures.user :as user-fixture]
            [fixtures.util :as fixture-util]
            [masques.model.user :as user-model]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(defn login []
  (when-not (user-model/login "test-user" (.toCharArray "password"))
    (throw (RuntimeException. "Failed to login as a test user."))))

(defn logout []
  (user-model/logout))

(defn login-fixture [function]
  (try
    (login) 
    (function)
    (finally
      (logout))))

(defn create-combined-login-fixture [other-fixture-maps]
  (clojure-test/join-fixtures [(fixture-util/create-fixture other-fixture-maps)
                               (fixture-util/create-fixture-fn user-fixture/fixture-map)
                               login-fixture]))

(defn use-combined-login-fixture [& fixture-maps]
  (clojure-test/use-fixtures :once (create-combined-login-fixture fixture-maps)))

(defn create-test-window [panel]
  (view-utils/center-window
    (seesaw-core/frame
      :title "Test Window"
      :content panel)))

(defn show [panel]
  (seesaw-core/show!
    (create-test-window panel)))

(defn show-and-wait
  ([panel] (show-and-wait panel 5000))
  ([panel wait-time]
    (let [test-frame (show panel)]
      (Thread/sleep wait-time)
      (.hide test-frame)
      (.dispose test-frame))))