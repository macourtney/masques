(ns masques.model.test.share
  (:require test.init)
  (:require [clojure.tools.logging :as logging]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.message :as message-model]
            [masques.model.profile :as profile-model])
  (:use clojure.test
        masques.model.base
        masques.model.share))

(def ^:dynamic test-message-record {
                                :subject "The subject, G" 
                                :body "Nice body you got there..."
                              })

(def ^:dynamic test-share-record { :content-type "message" })

(def ^:dynamic test-friend-request { :content-type "friend" })

(def test-destination "gfgHVoVFMYpBJwuL04mRa-~vQBt-p5lYfyVW2JatrzuJBy3Z7DgkxF68zDQe4M9uD-zOoBCBWctioFoUjnzPbDbflacwPvLnNxN-2GB64b73vDNPKkffM1JXLn6cRLWurxTYVeXaZns7ZmVj969XM3tOwEny1JZbMm-24YIaUwb66vkLeM33uanMer~II--OuikXx654ZkMXAORoJSu3hb04Q2s8sMR6-dnABeijfKShzINDg-JZSCRxWIay~VidFF6nhpi-BO3HPHPfGYTPkN5-w08z0IEaTeoBLNBBfVrmXwy2xPQWK1px2IRMpf0J~EiOf300Gin9xEoAhjEeL0LtUshT4bX2J1c~WiMHNGRJfjw4YspNVr8sDLOcVziOshLORlYDwkV6~ZNmovRQdcnwQ9OZzfM16fYib7Xb2wVtk-5TGIJOJiaegazOIb7Ze71N~EGX6epvwU1m2eGZ2I6oe~i2MOekHmnqhvolC1MQTUNGi-temb17xaeomMAdAAAA")

(def profile-map {
  profile-model/alias-key "Ted"
  profile-model/avatar-path-key "./test/support_files/avatar.png"
  profile-model/destination-key test-destination
  profile-model/private-key-key "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJPZamGpI82xY5dYI4Is+dVw1jaRC0eCnOkiv+E75hdLCAfY6Nm25ftj/UgssJka1BwQpvWrsASCjVSUK3aSGBNVf59RzdB7rprtjbHQJsFHZ7gwzkFI2UccLdOdHRQaJjypfhhC39Z7LTa7ErDm5Wlq664QPbxL89csJSsBh4IDAgMBAAECgYA7sofv0vmv7jZGP8Jmp35hHmSAN+SUBTsSL4PGkAcB1LvzXzP15JHMBb2ZTOIpj9mhU1/2xlIWIBis0/8Qq0CovdpS+3AD7jhNjbdA3Rv28qsqa0D/hbpniAkA/ezkpNDF8Ag/gbDv20zB5shj/gpjpv4pPY8LOp1YcfoCJLd6oQJBAOuEnxyq0tXKGqtbbcR449+3cwhJZlwA4jgeVx1e5NReKbtlUcB4N1HDUY2MtwjesTXBZJAwtt811f0xjFC4sxsCQQCgtQJbiKdo05LBbJ2Cc5shWwyAdqpLnW1Sku4aubqfkHCRqs2SeqVK/yhDdjMTni6+GKiK8PpDlRd1yDhUf/M5AkA+UGy47QmzvzGnPR2h6kqAms042BLZLPKt3nk2MDFjbzajen9S6XvZilA9n4meMy24B19QN1NrY5cm0sFJalUZAkAec23o7jHaeQx7vhryVvl0Do6F4PZPsZq/ZLvdMIgeJ/5Me7LMKJUdas+0SLdQ5k4xEvcMrLCfEacKWE/kIwJxAkBGGAsrMuqf6DTZHm5mxiBJxuctymY44V1r7UjB1F0jIE4BhLUKe2PNYCKcr84vwA6etdqPF+MyCmelpcVCAb2y"
  profile-model/private-key-algorithm-key "RSA"
  profile-model/identity-key "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCT2WphqSPNsWOXWCOCLPnVcNY2kQtHgpzpIr/hO+YXSwgH2OjZtuX7Y/1ILLCZGtQcEKb1q7AEgo1UlCt2khgTVX+fUc3Qe66a7Y2x0CbBR2e4MM5BSNlHHC3TnR0UGiY8qX4YQt/Wey02uxKw5uVpauuuED28S/PXLCUrAYeCAwIDAQAB"
  profile-model/identity-algorithm-key "RSA" })

(defn share-test-fixture [function]
  (binding [test-message-record (message-model/find-message
                                  (message-model/save test-message-record))]
    (binding [test-share-record (find-share
                                  (save (assoc test-share-record :message-id
                                               (:id test-message-record))))
              test-friend-request (find-share (save test-friend-request))]
      (function)
      (delete-share test-friend-request)
      (delete-share test-share-record))
    (message-model/delete-message test-message-record)))

(use-fixtures :once share-test-fixture)

(deftest test-add-share
  (is test-share-record)
  (is (:id test-share-record))
  (is (= (:content-type test-share-record) "message"))
  (is (:message-id test-share-record))
  (is (not (nil? (:uuid test-share-record))))
  (is (instance? org.joda.time.DateTime (:created-at test-share-record))))

(deftest test-load-share-with-content
  (let [test-share-record (get-and-build (:id test-share-record))]
    (is (map? test-share-record))
    (is (map? (:message test-share-record)))))

(deftest test-create-friend-request-share
  (let [test-message "test-message"
        test-profile (profile-model/load-masque-map
                         (profile-model/create-masque-map profile-map))
        test-request (friend-request-model/find-friend-request
                       (friend-request-model/save
                         { friend-request-model/request-status-key
                             friend-request-model/pending-sent-status
                           friend-request-model/profile-id-key
                             (id test-profile) }))
        test-share (find-share
                     (create-send-friend-request-share test-message test-profile
                                                       test-request))
        test-message-id (message-id test-share)]
    (is test-share)
    (is (is-friend-request test-share))
    (is (= (content-id-key test-share) (id test-request)))
    (is (= (profile-to-id-key test-share) (id test-profile)))
    (is (= (id (find-friend-request-share-with-to-profile test-profile))
           (id test-share)))
    (let [test-message2 "test-message2"
          test-share2 (create-send-friend-request-share
                        test-message2 test-profile test-request)
          test-message-id2 (message-id test-share2)]
      (is test-share2)
      (is (= (id test-share2) (id test-share)))
      (is (= test-message-id2 test-message-id))
      (is (= (message-model/body test-message-id) test-message2))
      (is (is-friend-request test-share2))
      (is (= (content-id-key test-share2) (id test-request)))
      (is (= (profile-to-id-key test-share2) (id test-profile))))
    (delete-share test-share)
    (profile-model/delete-profile test-profile)
    (is (nil? (message-model/find-message test-message-id)))
    (is (nil? (friend-request-model/find-friend-request (id test-request))))))

(deftest test-other-profile
  (let [saved-other-profile (profile-model/find-profile
                              (profile-model/save profile-map))]
    (let [test-share { profile-from-id-key (id saved-other-profile)
                       profile-to-id-key (id (profile-model/current-user)) }]
      (is (= saved-other-profile (other-profile test-share))))
    (let [test-share { profile-from-id-key (id (profile-model/current-user))
                       profile-to-id-key (id saved-other-profile) }]
      (is (= saved-other-profile (other-profile test-share))))))