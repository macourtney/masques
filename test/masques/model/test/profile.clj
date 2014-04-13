(ns masques.model.test.profile
  (:require test.init
            [clj-i2p.core :as clj-i2p]
            [clojure.java.io :as io]
            [config.db-config :as db-config]
            [korma.core :as korma]
            [masques.model.base :as model-base]
            [masques.test.util :as test-util])
  (:use clojure.test
        masques.model.profile)
  (:import [java.io PushbackReader]))

(def test-masques-id-file
  (io/file test-util/test-support-files-directory "ted.mid"))

(def test-destination "gfgHVoVFMYpBJwuL04mRa-~vQBt-p5lYfyVW2JatrzuJBy3Z7DgkxF68zDQe4M9uD-zOoBCBWctioFoUjnzPbDbflacwPvLnNxN-2GB64b73vDNPKkffM1JXLn6cRLWurxTYVeXaZns7ZmVj969XM3tOwEny1JZbMm-24YIaUwb66vkLeM33uanMer~II--OuikXx654ZkMXAORoJSu3hb04Q2s8sMR6-dnABeijfKShzINDg-JZSCRxWIay~VidFF6nhpi-BO3HPHPfGYTPkN5-w08z0IEaTeoBLNBBfVrmXwy2xPQWK1px2IRMpf0J~EiOf300Gin9xEoAhjEeL0LtUshT4bX2J1c~WiMHNGRJfjw4YspNVr8sDLOcVziOshLORlYDwkV6~ZNmovRQdcnwQ9OZzfM16fYib7Xb2wVtk-5TGIJOJiaegazOIb7Ze71N~EGX6epvwU1m2eGZ2I6oe~i2MOekHmnqhvolC1MQTUNGi-temb17xaeomMAdAAAA")

(def profile-map {
  alias-key "Ted"
  avatar-path-key "./test/support_files/avatar.png"
  destination-key test-destination
  private-key-key "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJPZamGpI82xY5dYI4Is+dVw1jaRC0eCnOkiv+E75hdLCAfY6Nm25ftj/UgssJka1BwQpvWrsASCjVSUK3aSGBNVf59RzdB7rprtjbHQJsFHZ7gwzkFI2UccLdOdHRQaJjypfhhC39Z7LTa7ErDm5Wlq664QPbxL89csJSsBh4IDAgMBAAECgYA7sofv0vmv7jZGP8Jmp35hHmSAN+SUBTsSL4PGkAcB1LvzXzP15JHMBb2ZTOIpj9mhU1/2xlIWIBis0/8Qq0CovdpS+3AD7jhNjbdA3Rv28qsqa0D/hbpniAkA/ezkpNDF8Ag/gbDv20zB5shj/gpjpv4pPY8LOp1YcfoCJLd6oQJBAOuEnxyq0tXKGqtbbcR449+3cwhJZlwA4jgeVx1e5NReKbtlUcB4N1HDUY2MtwjesTXBZJAwtt811f0xjFC4sxsCQQCgtQJbiKdo05LBbJ2Cc5shWwyAdqpLnW1Sku4aubqfkHCRqs2SeqVK/yhDdjMTni6+GKiK8PpDlRd1yDhUf/M5AkA+UGy47QmzvzGnPR2h6kqAms042BLZLPKt3nk2MDFjbzajen9S6XvZilA9n4meMy24B19QN1NrY5cm0sFJalUZAkAec23o7jHaeQx7vhryVvl0Do6F4PZPsZq/ZLvdMIgeJ/5Me7LMKJUdas+0SLdQ5k4xEvcMrLCfEacKWE/kIwJxAkBGGAsrMuqf6DTZHm5mxiBJxuctymY44V1r7UjB1F0jIE4BhLUKe2PNYCKcr84vwA6etdqPF+MyCmelpcVCAb2y"
  private-key-algorithm-key "RSA"
  identity-key "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCT2WphqSPNsWOXWCOCLPnVcNY2kQtHgpzpIr/hO+YXSwgH2OjZtuX7Y/1ILLCZGtQcEKb1q7AEgo1UlCt2khgTVX+fUc3Qe66a7Y2x0CbBR2e4MM5BSNlHHC3TnR0UGiY8qX4YQt/Wey02uxKw5uVpauuuED28S/PXLCUrAYeCAwIDAQAB"
  identity-algorithm-key "RSA" })

(deftest test-add-profile
  (let [profile-record (save profile-map)]
    (is profile-record)
    (is (:id profile-record))
    (is (= (alias-key profile-record) (alias-key profile-map)))
    (is (instance? org.joda.time.DateTime (:created-at profile-record)))))

(deftest test-build-profile
  (let [built-profile (build (:id (save profile-map)))]
    ;(println "\n\nBUILT PROFILE\n\n" built-profile)
    (is built-profile)
    (is (map? (:avatar built-profile)))
    (is (= (:id (:avatar built-profile)) (:avatar-file-id built-profile)))
    (is (= (:path (:avatar built-profile)) (:avatar-path profile-map)))))

(deftest test-create-user-profile
  (let [test-alias "Ted"
        user-profile (create-user test-alias)]
    (is user-profile)
    (is (= (alias-key user-profile) test-alias))
    (delete-profile user-profile)))

(deftest test-init
  (is (nil? (current-user)))
  (init)
  (let [old-user (current-user)]
    (logout)
    (is (nil? (current-user)))
    (delete-profile old-user)))

(deftest test-create-masques-id-map
  (is (not (nil? (clj-i2p/base-64-destination))))
  (is (= (create-masques-id-map profile-map)
         { alias-key (alias-key profile-map)
           identity-key (identity-key profile-map)
           identity-algorithm-key (identity-algorithm-key profile-map)
           destination-key (clj-i2p/base-64-destination)})))

(deftest test-create-masques-id-file
  (when (.exists test-masques-id-file)
    (io/delete-file test-masques-id-file))
  (create-masques-id-file test-masques-id-file profile-map)
  (is (.exists test-masques-id-file))
  (is (= (read-masques-id-file test-masques-id-file) 
         (create-masques-id-map profile-map)))
  (io/delete-file test-masques-id-file))

(deftest test-load-masques-id-map
  (let [profile (load-masques-id-map (create-masques-id-map profile-map))]
    (is (= (count (korma/select model-base/profile)) 1))
    (is profile)
    (is (= (alias-key profile) (alias-key profile-map)))
    (is (= (destination-key profile) test-destination))
    (is (= (identity-key profile) (identity-key profile-map)))
    (is (= (identity-algorithm-key profile) (identity-algorithm-key profile-map)))
    (let [profile2 (load-masques-id-map (create-masques-id-map profile-map))]
      (is (= (model-base/id profile) (model-base/id profile2)))
      (is (= (count (korma/select model-base/profile)) 1)))
    (delete-profile profile)
    (is (= (count (korma/select model-base/profile)) 0))))

(deftest test-load-masques-id-file
  (when (.exists test-masques-id-file)
    (io/delete-file test-masques-id-file))
  (create-masques-id-file test-masques-id-file profile-map)
  (let [profile (load-masques-id-file test-masques-id-file)]
    (is (= (count (korma/select model-base/profile)) 1))
    (is profile)
    (is (= (alias-key profile) (alias-key profile-map)))
    (is (= (destination-key profile) test-destination))
    (is (= (identity-key profile) (identity-key profile-map)))
    (is (= (identity-algorithm-key profile) (identity-algorithm-key profile-map)))
    (delete-profile profile)
    (io/delete-file test-masques-id-file)
    (is (= (count (korma/select model-base/profile)) 0))))

(deftest test-alias
  (is (= (alias-key profile-map) (alias profile-map)))
  (let [saved-profile (save profile-map)]
    (is (= (alias-key profile-map) (alias (:id saved-profile))))
    (delete-profile saved-profile)))

(deftest test-all-destinations
  (let [profile (load-masques-id-map (create-masques-id-map profile-map))
        destinations (all-destinations)]
    (is destinations)
    (is (= (count destinations) 1))
    (is (= destinations [test-destination]))
    (delete-profile profile)
    (is (= (count (korma/select model-base/profile)) 0))))