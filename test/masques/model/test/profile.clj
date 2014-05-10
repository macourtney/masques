(ns masques.model.test.profile
  (:refer-clojure :exclude [identity alias])
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
  (let [initial-profile-count (count (all-profile-ids))
        profile-record (save profile-map)]
    (is profile-record)
    (is (:id profile-record))
    (is (= (alias-key profile-record) (alias-key profile-map)))
    (is (instance? org.joda.time.DateTime (:created-at profile-record)))
    (delete-profile profile-record)
    (is (= (count (all-profile-ids)) initial-profile-count))))

(deftest test-build-profile
  (let [initial-profile-count (count (all-profile-ids))
        profile-record (save profile-map)
        built-profile (build (:id profile-record))]
    (is built-profile)
    (is (map? (:avatar built-profile)))
    (is (= (:id (:avatar built-profile)) (:avatar-file-id built-profile)))
    (is (= (:path (:avatar built-profile)) (:avatar-path profile-map)))
    (delete-profile profile-record)
    (is (= (count (all-profile-ids)) initial-profile-count))))

(deftest test-create-masques-id-map
  (let [initial-profile-count (count (all-profile-ids))]
    (is (not (nil? (clj-i2p/base-64-destination))))
    (is (= (create-masque-map profile-map)
           { alias-key (alias-key profile-map)
             identity-key (identity-key profile-map)
             identity-algorithm-key (identity-algorithm-key profile-map)
             destination-key (clj-i2p/base-64-destination)}))
    (is (= (count (all-profile-ids)) initial-profile-count))))

(deftest test-create-masque-file
  (let [initial-profile-count (count (all-profile-ids))]
    (when (.exists test-util/test-masque-file)
      (io/delete-file test-util/test-masque-file))
    (create-masque-file test-util/test-masque-file profile-map)
    (is (.exists test-util/test-masque-file))
    (is (= (read-masque-file test-util/test-masque-file) 
           (create-masque-map profile-map)))
    (io/delete-file test-util/test-masque-file)
    (is (= (count (all-profile-ids)) initial-profile-count))))

(deftest test-load-masque-map
  (let [initial-profile-count (count (all-profile-ids))
        profile (load-masque-map (create-masque-map profile-map))]
    (is (= (count (all-profile-ids)) (inc initial-profile-count)))
    (is profile)
    (is (= (alias-key profile) (alias-key profile-map)))
    (is (= (destination-key profile) test-destination))
    (is (= (identity-key profile) (identity-key profile-map)))
    (is (= (identity-algorithm-key profile) (identity-algorithm-key profile-map)))
    (let [profile2 (load-masque-map (create-masque-map profile-map))]
      (is (= (model-base/id profile) (model-base/id profile2)))
      (is (= (count (all-profile-ids)) (inc initial-profile-count))))
    (delete-profile profile)
    (is (= (count (all-profile-ids)) initial-profile-count))))

(deftest test-load-masque-file
  (let [initial-profile-count (count (all-profile-ids))]
    (when (.exists test-util/test-masque-file)
      (io/delete-file test-util/test-masque-file))
    (create-masque-file test-util/test-masque-file profile-map)
    (let [profile (load-masque-file test-util/test-masque-file)]
      (is (= (count (all-profile-ids)) (inc initial-profile-count)))
      (is profile)
      (is (= (alias-key profile) (alias-key profile-map)))
      (is (= (destination-key profile) test-destination))
      (is (= (identity-key profile) (identity-key profile-map)))
      (is (= (identity-algorithm-key profile)
             (identity-algorithm-key profile-map)))
      (delete-profile profile)
      (io/delete-file test-util/test-masque-file))
    (is (= (count (all-profile-ids)) initial-profile-count))))

(deftest test-alias
  (let [initial-profile-count (count (all-profile-ids))]
    (is (= (alias-key profile-map) (alias profile-map)))
    (let [saved-profile (save profile-map)]
      (is (= (alias-key profile-map) (alias (:id saved-profile))))
      (delete-profile saved-profile))
    (is (= (count (all-profile-ids)) initial-profile-count))))

(deftest test-all-destinations
  (let [initial-profile-count (count (all-profile-ids))]
    (let [profile (load-masque-map (create-masque-map profile-map))
          destinations (all-destinations)]
      (is destinations)
      (is (= (count destinations) (inc initial-profile-count)))
      (is (= (filter clojure.core/identity destinations) [test-destination]))
      (delete-profile profile))
    (is (= (count (all-profile-ids)) initial-profile-count))))