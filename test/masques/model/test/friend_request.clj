(ns masques.model.test.friend-request
  (:require test.init
            [clojure.java.io :as io]
            [masques.model.profile :as profile]
            [masques.test.util :as test-util])
  (:use clojure.test
        masques.model.base
        masques.model.friend-request))

(def test-masques-id-file
  (io/file test-util/test-support-files-directory "ted.mid"))

(def profile-map {
  profile/alias-key "Ted"
  profile/avatar-path-key "./test/support_files/avatar.png"
  profile/private-key-key "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJPZamGpI82xY5dYI4Is+dVw1jaRC0eCnOkiv+E75hdLCAfY6Nm25ftj/UgssJka1BwQpvWrsASCjVSUK3aSGBNVf59RzdB7rprtjbHQJsFHZ7gwzkFI2UccLdOdHRQaJjypfhhC39Z7LTa7ErDm5Wlq664QPbxL89csJSsBh4IDAgMBAAECgYA7sofv0vmv7jZGP8Jmp35hHmSAN+SUBTsSL4PGkAcB1LvzXzP15JHMBb2ZTOIpj9mhU1/2xlIWIBis0/8Qq0CovdpS+3AD7jhNjbdA3Rv28qsqa0D/hbpniAkA/ezkpNDF8Ag/gbDv20zB5shj/gpjpv4pPY8LOp1YcfoCJLd6oQJBAOuEnxyq0tXKGqtbbcR449+3cwhJZlwA4jgeVx1e5NReKbtlUcB4N1HDUY2MtwjesTXBZJAwtt811f0xjFC4sxsCQQCgtQJbiKdo05LBbJ2Cc5shWwyAdqpLnW1Sku4aubqfkHCRqs2SeqVK/yhDdjMTni6+GKiK8PpDlRd1yDhUf/M5AkA+UGy47QmzvzGnPR2h6kqAms042BLZLPKt3nk2MDFjbzajen9S6XvZilA9n4meMy24B19QN1NrY5cm0sFJalUZAkAec23o7jHaeQx7vhryVvl0Do6F4PZPsZq/ZLvdMIgeJ/5Me7LMKJUdas+0SLdQ5k4xEvcMrLCfEacKWE/kIwJxAkBGGAsrMuqf6DTZHm5mxiBJxuctymY44V1r7UjB1F0jIE4BhLUKe2PNYCKcr84vwA6etdqPF+MyCmelpcVCAb2y",
  profile/private-key-algorithm-key "RSA",
  profile/identity-key "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCT2WphqSPNsWOXWCOCLPnVcNY2kQtHgpzpIr/hO+YXSwgH2OjZtuX7Y/1ILLCZGtQcEKb1q7AEgo1UlCt2khgTVX+fUc3Qe66a7Y2x0CbBR2e4MM5BSNlHHC3TnR0UGiY8qX4YQt/Wey02uxKw5uVpauuuED28S/PXLCUrAYeCAwIDAQAB",
  profile/identity-algorithm-key "RSA",
  })

(def request-map {:request-status "pending"})

(def big-request {:request-status "pending"
                  :message {:subject "Let's be friends on Masques!"
                            :body "I met you at the party. I had on the blue hat."}
                  :profile {:alias "Ted"}})

(defn request-tester [request-record]
  (is request-record)
  (is (:id request-record))
  (is (integer? (:id request-record)))
  (is (= (:request-status request-record) "pending"))
  (is (instance? org.joda.time.DateTime (:created-at request-record))))

(deftest test-save-request
  (let [request-record (save request-map)]
    (request-tester request-record)))

(deftest test-find-request
  (let [request-record (save request-map)]
    (request-tester request-record)))

(deftest test-send-request
  (profile/create-masques-id-file test-masques-id-file profile-map)
  (let [friend-request (send-request test-masques-id-file)]
    (is friend-request)
    (is (= (request-status-key friend-request) pending-status))
    (is (requested-at-key friend-request))
    (let [profile-id (profile-id-key friend-request)]
      (is profile-id)
      (is (profile/find-profile profile-id)))
    (delete-friend-request friend-request))
  (io/delete-file test-masques-id-file))

