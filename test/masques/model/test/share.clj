(ns masques.model.test.share
  (:require test.init)
  (:require [clojure.tools.logging :as logging]
            [masques.model.grouping :as grouping-model]
            [masques.model.friend-request :as friend-request-model]
            [masques.model.message :as message-model]
            [masques.model.profile :as profile-model]
            [masques.model.share-profile :as share-profile-model]
            [masques.test.util :as test-util])
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
      (test-util/login)
      (function)
      (test-util/logout)
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
    (is (= (share-profile-model/first-profile-id-for-share test-share)
           (id test-profile)))
    (is (= (id (find-friend-request-share-with-to-profile test-profile))
           (id test-share)))
    (let [test-message2 "test-message2"
          test-share2 (find-share
                        (create-send-friend-request-share
                          test-message2 test-profile test-request))
          test-message-id2 (message-id test-share2)]
      (is test-share2)
      (is (= (id test-share2) (id test-share)))
      (is (= test-message-id2 test-message-id))
      (is (= (message-model/body test-message-id) test-message2))
      (is (is-friend-request test-share2))
      (is (= (content-id-key test-share2) (id test-request)))
      (is (= (share-profile-model/first-profile-id-for-share test-share2)
             (id test-profile))))
    (delete-share test-share)
    (profile-model/delete-profile test-profile)
    (is (nil? (message-model/find-message test-message-id)))
    (is (nil? (friend-request-model/find-friend-request (id test-request))))))

(deftest test-first-other-profile
  (let [saved-other-profile (profile-model/find-profile
                              (profile-model/save profile-map))]
    (let [test-share (create-status-share "test" nil [saved-other-profile])]
      (is (= saved-other-profile (first-other-profile test-share)))
      (delete-share test-share))
    (let [test-share (create-received-share "test" (id saved-other-profile) "")]
      (is (= saved-other-profile (first-other-profile test-share)))
      (delete-share test-share))))

(deftest test-create-status-share
  (let [test-message "New status!"
        test-profile (profile-model/load-masque-map
                         (profile-model/create-masque-map profile-map))
        test-share (find-share
                     (create-status-share test-message nil [test-profile]))]
    (is test-share)
    (is (= (content-type-key test-share) status-type))
    (is (= (message-model/body
             (message-model/find-message (message-id-key test-share)))
           test-message))
    (is (= (share-profile-model/first-profile-id-for-share test-share)
           (id test-profile)))
    (is (= (profile-from-id-key test-share) (id (profile-model/current-user))))
    (delete-share test-share)
    (profile-model/delete-profile test-profile))
  (let [test-message "New status!"
        test-group (grouping-model/find-grouping
                     (grouping-model/create-user-group "Test Group"))
        test-share (find-share
                     (create-status-share test-message [test-group] nil))]
    (is test-share)
    (is (= (content-type-key test-share) status-type))
    (is (= (message-model/body
             (message-model/find-message (message-id-key test-share)))
           test-message))
    (is (nil? (share-profile-model/first-profile-id-for-share test-share)))
    (is (= (profile-from-id-key test-share) (id (profile-model/current-user))))
    (delete-share test-share)
    (grouping-model/delete-grouping test-group)))

(deftest test-create-received-share
  (let [test-message "New status!"
        test-profile (profile-model/load-masque-map
                         (profile-model/create-masque-map profile-map))
        test-uuid "test-uuid"
        test-share (find-share
                     (create-received-share test-message test-profile
                                            test-uuid))]
    (is test-share)
    (is (= (content-type-key test-share) status-type))
    (is (= (uuid test-share) test-uuid))
    (is (= (message-model/body
             (message-model/find-message (message-id-key test-share)))
           test-message))
    (is (= (share-profile-model/first-profile-id-for-share test-share)
           (id (profile-model/current-user))))
    (is (= (profile-from-id-key test-share) (id test-profile)))
    (delete-share test-share)
    (profile-model/delete-profile test-profile)))

(deftest test-stream-functions
  (let [test-share (create-share { :content-type status-type })
        test-profile (profile-model/find-profile (profile-model/current-user))
        test-profile-id (id test-profile)
        test-share-profile-id (share-profile-model/create-share-profile
                                test-share test-profile-id)
        test-share-profile (share-profile-model/find-share-profile
                             test-share-profile-id)]
    (is test-share)
    (is test-profile)
    (is test-profile-id)
    (is test-share-profile-id)
    (is test-share-profile)
    (is (= (count-stream-shares) 1))
    (is (= (find-stream-share-at 0) (find-share test-share)))
    (is (= (index-of-stream-share test-share) 0))
    (let [test-share2 (create-share { :content-type status-type })
          test-profile2 (profile-model/find-profile (profile-model/current-user))
          test-profile-id2 (id test-profile2)
          test-share-profile-id2 (share-profile-model/create-share-profile
                                   test-share2 test-profile-id2)
          test-share-profile2 (share-profile-model/find-share-profile
                                test-share-profile-id2)]
      (is (= (count-stream-shares) 2))
      (is (= (find-stream-share-at 1) (find-share test-share2)))
      (is (= (index-of-stream-share test-share2) 1))
      (share-profile-model/delete-all test-share2)
      (share-profile-model/delete-share-profile test-share-profile2)
      (delete-share test-share2))
    (share-profile-model/delete-all test-share)
    (share-profile-model/delete-share-profile test-share-profile)
    (delete-share test-share)))