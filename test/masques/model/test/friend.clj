(ns masques.model.test.friend
  (:require [clojure.data.xml :as data-xml]
            [clojure.java.io :as io]
            [fixtures.identity :as fixtures-identity]
            [fixtures.user :as fixtures-user]
            [fixtures.util :as fixtures-util]
            [masques.model.user :as user-model]) 
  (:use clojure.test
        masques.model.friend))

(def test-identity (first fixtures-identity/records))
(def test-identity-2 (second fixtures-identity/records))

(def test-destination "LlC5T8BJovJ2TONm1NuJ4KdmwhFeSRtajxncTi3YvAQeRIvMUqq7IcSTAf5HZiAsKvprZTZa1SncxiCcNivxbQgHZ0sy~AkDOpURrN3BRdQqQn2b8qhYWgs~xvt-Yn7ECrXSgpR7AKjhoFW6~AtiXGSxTdbQafmlZnuwivnzJIb29BUsUx0nOBmcG918nQtethnxnmnTKqLqFBc5c2qP6evP2xYrvWwGaTM4QPidzq-aqEoWUkc1rdkozqWd~M2A0WhNGAjB432Jpp9N8KCacE6SEPM~uKOSsvQtPPZk~9V3UYnDU0941HhhHZgaHZpIy7yeDKkZCGqUMTMh1yEPYwqpOfHbFraoldALDugKz~NkJ0QVL~jxCh40xxnBTBhLsCJuzTe~FfL4odl1vtmwVlACMhaNBHqOaBgKGqUssqmfC1TdLkswnSOni7luA8RZHVgmRI0MnzlHHwg9lHdY53w7Nok1X404OzaWCNy75-bP9po-1DTax4IBNFDpvHrcAAAA")

(def test-friend { :identity_id (:id test-identity) :friend_id (:id test-identity-2) })

(def test-user (first fixtures-user/records))

(fixtures-util/use-fixture-maps :once fixtures-identity/fixture-map)

(deftest test-all-friends
  (let [friend-id (insert test-friend)]
    (is friend-id)
    (try
      (let [test-friends (all-friends test-identity)]
        (is (= [(assoc test-friend :id friend-id)] test-friends)))
      (finally
        (when friend-id
          (destroy-record { :id friend-id }))))))

(deftest test-add-friend
  (is (nil? (add-friend test-identity nil)))
  (is (nil? (add-friend nil test-identity-2)))
  (is (nil? (add-friend nil nil)))
  (is (empty? (all-friends test-identity)))
  (let [friend-id (add-friend test-identity-2 test-identity)]
    (is friend-id)
    (try
      (is (friend? test-identity-2 test-identity))
      (let [test-friends (all-friends test-identity)]
        (is (= [(assoc test-friend :id friend-id)] test-friends)))
      (finally
        (when friend-id
          (remove-friend test-identity-2 test-identity)
          (is (not (find-record { :id friend-id }))))))))

(deftest test-friend-xml
  (is (nil? (friend-xml nil test-destination)))
  (is (nil? (friend-xml test-user nil)))
  (is (nil? (friend-xml nil nil)))
  (let [test-xml (friend-xml test-user test-destination)]
    (is test-xml)
    (is (= (first (:content test-xml)) (user-model/xml test-user)))
    (is (= (first (:content (second (:content test-xml)))) test-destination))))

(deftest test-parse-destination-xml
  (is (nil? (parse-destination-xml (data-xml/element :fail {} test-destination))))
  (is (nil? (parse-destination-xml (data-xml/element :destination {}))))
  (is (nil? (parse-destination-xml nil)))
  (let [destination-xml (data-xml/element :destination {} test-destination)
        parsed-destination (parse-destination-xml destination-xml)]
    (is parsed-destination)
    (is (= parsed-destination test-destination))))

(deftest test-load-friend-xml
  (is (nil? (load-friend-xml
              (data-xml/element :fail {}
                                (data-xml/element :user { :name "test-user" :publicKey "" :publicKeyAlgorithm "RSA" })
                                (data-xml/element :destination {} test-destination)))))
  (is (nil? (load-friend-xml
              (data-xml/element :friend {}
                                (data-xml/element :user { :name "test-user" :publicKey "" :publicKeyAlgorithm "RSA" })))))
  (is (nil? (load-friend-xml
              (data-xml/element :friend {} (data-xml/element :destination {} test-destination)))))
  (is (nil? (load-friend-xml (data-xml/element :friend {}))))
  (is (nil? (load-friend-xml nil)))
  (let [friend-xml (data-xml/element :friend {}
                                     (data-xml/element :user { :name "test-user" :publicKey "" :publicKeyAlgorithm "RSA" })
                                     (data-xml/element :destination {} test-destination))
        friend-id (load-friend-xml friend-xml test-identity)]
    (is friend-id)
    (when friend-id
      (destroy-record { :id friend-id }))))

(deftest test-write-friend-xml
  (let [test-file (io/as-file "./test/test_friend.xml")]
    (write-friend-xml test-file test-user test-destination)
    (is (.exists test-file))
    (when (.exists test-file)
      (.delete test-file))))