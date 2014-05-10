(ns masques.test.util
  (:require [clj-i2p.core :as clj-i2p]
            [clojure.java.io :as io]
            [clojure.test :as clojure-test]
            [config.db-config :as db-config]
            [fixtures.profile :as profile-fixture]
            [fixtures.util :as fixture-util]
            [masques.model.profile :as profile-model]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(def test-support-files-directory (io/file "./test/support_files/"))

(def test-destination-str "pR-QlZfMy3edvvUVMyLBsHvONlskEjWVHk6LPzp3UCEKyx9l6y7AYsGPlUprlfb9QiDQIamxxy6ohsArHvHpdrrFD0fOe9SEWicqnm0VFwY8v6gib-HR5TA~19jcvzqxPKM2v1i4NLVofmbR0b-e~zsdC8~QrY4W8PGeY56lQaSWn9SqPj06EqudaI8VJdkiyTUnvUZY0ReZP5Hn4Bec47QCmL6njtd9UCNkK0jrrmlN0kXBBNH1ICfQa89HAvBE3S7IC2joJXCw1mdr7J9JHqies9DqVEKMFAqC0KHVQvR7MYn47OwIGIcxQm~tKLU~qyMqbdSUsA66JQJCquVjSE~pIU6KxgD-5vz4Dz9tohpI9bQiUkSkyYeydv3pYLegODM~79l6kfszPiBi1Eq7aJhjvvzvV13FO4FjxKVPRJJfnr6vOnJjW2cEauNpriiY-GlOmcWePrro7fN2vceL1M7DlC28icYBZV4YBiwLZ4hr0soCyAS6oiB9P6EBYmNHAAAA")
(def test-destination (clj-i2p/as-destination test-destination-str))

(def test-masque-file
  (io/file test-support-files-directory "ted.mid"))

(def profile-map {
  profile-model/alias-key "Ted"
  profile-model/avatar-path-key "./test/support_files/avatar.png"
  profile-model/destination-key test-destination-str
  profile-model/private-key-key "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJPZamGpI82xY5dYI4Is+dVw1jaRC0eCnOkiv+E75hdLCAfY6Nm25ftj/UgssJka1BwQpvWrsASCjVSUK3aSGBNVf59RzdB7rprtjbHQJsFHZ7gwzkFI2UccLdOdHRQaJjypfhhC39Z7LTa7ErDm5Wlq664QPbxL89csJSsBh4IDAgMBAAECgYA7sofv0vmv7jZGP8Jmp35hHmSAN+SUBTsSL4PGkAcB1LvzXzP15JHMBb2ZTOIpj9mhU1/2xlIWIBis0/8Qq0CovdpS+3AD7jhNjbdA3Rv28qsqa0D/hbpniAkA/ezkpNDF8Ag/gbDv20zB5shj/gpjpv4pPY8LOp1YcfoCJLd6oQJBAOuEnxyq0tXKGqtbbcR449+3cwhJZlwA4jgeVx1e5NReKbtlUcB4N1HDUY2MtwjesTXBZJAwtt811f0xjFC4sxsCQQCgtQJbiKdo05LBbJ2Cc5shWwyAdqpLnW1Sku4aubqfkHCRqs2SeqVK/yhDdjMTni6+GKiK8PpDlRd1yDhUf/M5AkA+UGy47QmzvzGnPR2h6kqAms042BLZLPKt3nk2MDFjbzajen9S6XvZilA9n4meMy24B19QN1NrY5cm0sFJalUZAkAec23o7jHaeQx7vhryVvl0Do6F4PZPsZq/ZLvdMIgeJ/5Me7LMKJUdas+0SLdQ5k4xEvcMrLCfEacKWE/kIwJxAkBGGAsrMuqf6DTZHm5mxiBJxuctymY44V1r7UjB1F0jIE4BhLUKe2PNYCKcr84vwA6etdqPF+MyCmelpcVCAb2y"
  profile-model/private-key-algorithm-key "RSA",
  profile-model/identity-key "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCT2WphqSPNsWOXWCOCLPnVcNY2kQtHgpzpIr/hO+YXSwgH2OjZtuX7Y/1ILLCZGtQcEKb1q7AEgo1UlCt2khgTVX+fUc3Qe66a7Y2x0CbBR2e4MM5BSNlHHC3TnR0UGiY8qX4YQt/Wey02uxKw5uVpauuuED28S/PXLCUrAYeCAwIDAQAB"
  profile-model/identity-algorithm-key "RSA",
  })

(defn login []
  (profile-model/init))

(defn logout []
  (profile-model/logout))

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
                                  [(fixture-util/create-fixture 
                                     (cons profile-fixture/fixture-map
                                           fixture-maps))
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