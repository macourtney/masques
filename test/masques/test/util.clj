(ns masques.test.util
  (:require [clj-i2p.core :as clj-i2p]
            [clojure.test :as clojure-test]
            [fixtures.user :as user-fixture]
            [fixtures.util :as fixture-util]
            [masques.model.user :as user-model]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(def test-destination (clj-i2p/as-destination "pR-QlZfMy3edvvUVMyLBsHvONlskEjWVHk6LPzp3UCEKyx9l6y7AYsGPlUprlfb9QiDQIamxxy6ohsArHvHpdrrFD0fOe9SEWicqnm0VFwY8v6gib-HR5TA~19jcvzqxPKM2v1i4NLVofmbR0b-e~zsdC8~QrY4W8PGeY56lQaSWn9SqPj06EqudaI8VJdkiyTUnvUZY0ReZP5Hn4Bec47QCmL6njtd9UCNkK0jrrmlN0kXBBNH1ICfQa89HAvBE3S7IC2joJXCw1mdr7J9JHqies9DqVEKMFAqC0KHVQvR7MYn47OwIGIcxQm~tKLU~qyMqbdSUsA66JQJCquVjSE~pIU6KxgD-5vz4Dz9tohpI9bQiUkSkyYeydv3pYLegODM~79l6kfszPiBi1Eq7aJhjvvzvV13FO4FjxKVPRJJfnr6vOnJjW2cEauNpriiY-GlOmcWePrro7fN2vceL1M7DlC28icYBZV4YBiwLZ4hr0soCyAS6oiB9P6EBYmNHAAAA"))

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

(defn destination-fixture [function]
  (try
    (clj-i2p/set-destination test-destination)
    (clj-i2p/notify-destination-listeners)
    (function)
    (finally
      (clj-i2p/set-destination nil)
      (clj-i2p/notify-destination-listeners))))

(defn create-combined-login-fixture [fixtures-or-maps]
  (let [fixture-maps (filter map? fixtures-or-maps)]
    (clojure-test/join-fixtures (concat
                                  (filter fn? fixtures-or-maps)
                                  [(fixture-util/create-fixture (cons user-fixture/fixture-map fixture-maps))
                                   destination-fixture
                                   login-fixture]))))

(defn use-combined-login-fixture [& fixtures-or-maps]
  (clojure-test/use-fixtures :once (create-combined-login-fixture fixtures-or-maps)))

(defn mock-network-fixture [mock-network function]
  (try
    (clj-i2p/set-mock-network mock-network)
    (function)
    (finally
      (clj-i2p/clear-mock-network))))

(defn create-mock-network-fixture [mock-network]
  #(mock-network-fixture mock-network %))

(defn use-mock-network-fixture [mock-network]
  (clojure-test/use-fixtures :once (create-mock-network-fixture mock-network)))

(defn create-test-window [panel]
  (view-utils/center-window
    (seesaw-core/frame
      :title "Test Window"
      :content panel)))

(defn show
  ([panel] (show panel nil))
  ([panel init-fn]
    (let [frame (create-test-window panel)]
      (when init-fn
        (init-fn frame))
      (seesaw-core/show! frame))))

(defn show-and-wait
  ([panel] (show-and-wait panel 5000))
  ([panel wait-time]
    (let [test-frame (show panel)]
      (Thread/sleep wait-time)
      (.hide test-frame)
      (.dispose test-frame))))

(defn assert-show
  "Verifies the given frame is showing."
  [panel init-fn]
  (let [frame (show panel init-fn)]
    (Thread/sleep 100)
    (clojure-test/is frame)
    (clojure-test/is (.isShowing frame))
    frame))

(defn assert-close
  "Closes the given frame and verifies it is not showing."
  [frame]
  (.setVisible frame false)
  (.dispose frame)
  (Thread/sleep 100)
  (clojure-test/is (not (.isShowing frame))))