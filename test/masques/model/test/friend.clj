(ns masques.model.test.friend
  (:require [clojure.data.xml :as data-xml]
            [clojure.java.io :as io]
            [fixtures.identity :as fixtures-identity]
            [fixtures.group :as fixtures-group]
            [fixtures.group-permission :as fixtures-group-permission]
            [fixtures.name :as fixtures-name]
            [fixtures.permission :as fixtures-permission]
            [fixtures.user :as fixtures-user]
            [masques.model.group-membership :as group-membership-model]
            [masques.model.user :as user-model]
            [masques.test.util :as test-util]) 
  (:use clojure.test
        masques.model.friend))

(def test-identity (first fixtures-identity/records))
(def test-identity-2 (second fixtures-identity/records))

(def test-name (first fixtures-name/records))
(def test-name-2 (second fixtures-name/records))

(def test-destination "LlC5T8BJovJ2TONm1NuJ4KdmwhFeSRtajxncTi3YvAQeRIvMUqq7IcSTAf5HZiAsKvprZTZa1SncxiCcNivxbQgHZ0sy~AkDOpURrN3BRdQqQn2b8qhYWgs~xvt-Yn7ECrXSgpR7AKjhoFW6~AtiXGSxTdbQafmlZnuwivnzJIb29BUsUx0nOBmcG918nQtethnxnmnTKqLqFBc5c2qP6evP2xYrvWwGaTM4QPidzq-aqEoWUkc1rdkozqWd~M2A0WhNGAjB432Jpp9N8KCacE6SEPM~uKOSsvQtPPZk~9V3UYnDU0941HhhHZgaHZpIy7yeDKkZCGqUMTMh1yEPYwqpOfHbFraoldALDugKz~NkJ0QVL~jxCh40xxnBTBhLsCJuzTe~FfL4odl1vtmwVlACMhaNBHqOaBgKGqUssqmfC1TdLkswnSOni7luA8RZHVgmRI0MnzlHHwg9lHdY53w7Nok1X404OzaWCNy75-bP9po-1DTax4IBNFDpvHrcAAAA")

(def test-friend { :identity_id (:id test-identity) :friend_id (:id test-identity-2) })

(def test-user (first fixtures-user/records))

(def test-friend-file (io/as-file "./test/support_files/test_friend.xml"))

(def line-separator (System/getProperty "line.separator"))
(def test-friend-string (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?><friend>" line-separator "  <user name=\"test-user\" publicKey=\"\" publicKeyAlgorithm=\"RSA\"/>" line-separator "  <destination>LlC5T8BJovJ2TONm1NuJ4KdmwhFeSRtajxncTi3YvAQeRIvMUqq7IcSTAf5HZiAsKvprZTZa1SncxiCcNivxbQgHZ0sy~AkDOpURrN3BRdQqQn2b8qhYWgs~xvt-Yn7ECrXSgpR7AKjhoFW6~AtiXGSxTdbQafmlZnuwivnzJIb29BUsUx0nOBmcG918nQtethnxnmnTKqLqFBc5c2qP6evP2xYrvWwGaTM4QPidzq-aqEoWUkc1rdkozqWd~M2A0WhNGAjB432Jpp9N8KCacE6SEPM~uKOSsvQtPPZk~9V3UYnDU0941HhhHZgaHZpIy7yeDKkZCGqUMTMh1yEPYwqpOfHbFraoldALDugKz~NkJ0QVL~jxCh40xxnBTBhLsCJuzTe~FfL4odl1vtmwVlACMhaNBHqOaBgKGqUssqmfC1TdLkswnSOni7luA8RZHVgmRI0MnzlHHwg9lHdY53w7Nok1X404OzaWCNy75-bP9po-1DTax4IBNFDpvHrcAAAA</destination>" line-separator "</friend>" line-separator))

;(test-util/use-combined-login-fixture fixtures-name/fixture-map fixtures-group-permission/fixture-map)

(defn test-friend-listener [friend]
  friend)

(deftest test-add-listener
  (is (= (friend-add-listener-count) 0))
  (add-friend-add-listener test-friend-listener)
  (is (= (friend-add-listener-count) 1))
  (remove-friend-add-listener test-friend-listener)
  (is (= (friend-add-listener-count) 0)))

(deftest test-delete-listener
  (is (= (friend-delete-listener-count) 0))
  (add-friend-delete-listener test-friend-listener)
  (is (= (friend-delete-listener-count) 1))
  (remove-friend-delete-listener test-friend-listener)
  (is (= (friend-delete-listener-count) 0)))

;(deftest test-all-friends
;  (let [friend-id (insert test-friend)]
;    (is friend-id)
;    (try
;      (let [test-friends (all-friends test-identity)]
;        (is (= [(assoc test-friend :id friend-id)] test-friends)))
;      (finally
;        (when friend-id
;          (destroy-record { :id friend-id }))))))

;(deftest test-add-friend
;  (is (nil? (add-friend test-identity nil)))
;  (is (nil? (add-friend nil test-identity-2)))
;  (is (nil? (add-friend nil nil)))
;  (is (empty? (all-friends test-identity)))
;  (let [friend-id (add-friend test-identity-2 test-identity)]
;    (is friend-id)
;    (try
;      (is (friend? test-identity-2 test-identity))
;      (let [test-friends (all-friends test-identity)]
;        (is (= [(assoc test-friend :id friend-id)] test-friends)))
;      (finally
;        (when friend-id
;          (remove-friend test-identity-2 test-identity)
;          (is (not (find-record { :id friend-id }))))))))

(deftest test-friend-xml
  (is (nil? (friend-xml nil test-destination)))
  (is (nil? (friend-xml test-user nil)))
  (is (nil? (friend-xml nil nil)))
  (let [test-xml (friend-xml test-user test-destination)]
    (is test-xml)
    (is (= (first (:content test-xml)) (user-model/xml test-user)))
    (is (= (first (:content (second (:content test-xml)))) test-destination))))

(deftest test-friend-xml-string
  (is (nil? (friend-xml-string nil test-destination)))
  (is (nil? (friend-xml-string test-user nil)))
  (is (nil? (friend-xml-string nil nil)))
  (let [test-str (friend-xml-string test-user test-destination)]
    (is test-str)
    (is (= test-str test-friend-string))))

(deftest test-parse-destination-xml
  (is (nil? (parse-destination-xml (data-xml/element :fail {} test-destination))))
  (is (nil? (parse-destination-xml (data-xml/element :destination {}))))
  (is (nil? (parse-destination-xml nil)))
  (let [destination-xml (data-xml/element :destination {} test-destination)
        parsed-destination (parse-destination-xml destination-xml)]
    (is parsed-destination)
    (is (= parsed-destination test-destination))))

;(deftest test-load-friend-xml
;  (is (nil? (load-friend-xml
;              (data-xml/element :fail {}
;                                (data-xml/element :user { :name "test-user" :publicKey "" :publicKeyAlgorithm "RSA" })
;                                (data-xml/element :destination {} test-destination)))))
;  (is (nil? (load-friend-xml
;              (data-xml/element :friend {}
;                                (data-xml/element :user { :name "test-user" :publicKey "" :publicKeyAlgorithm "RSA" })))))
;  (is (nil? (load-friend-xml
;              (data-xml/element :friend {} (data-xml/element :destination {} test-destination)))))
;  (is (nil? (load-friend-xml (data-xml/element :friend {}))))
;  (is (nil? (load-friend-xml nil)))
;  (let [friend-xml (data-xml/element :friend {}
;                                     (data-xml/element :user { :name "test-user" :publicKey "" :publicKeyAlgorithm "RSA" })
;                                     (data-xml/element :destination {} test-destination))
;        friend-id (load-friend-xml friend-xml test-identity)]
;    (is friend-id)
;    (when friend-id
;      (destroy-record { :id friend-id }))))

(deftest test-write-friend-xml
  (let [test-file (io/as-file "./test/test_friend.xml")]
    (write-friend-xml test-file test-user test-destination)
    (is (.exists test-file))
    (when (.exists test-file)
      (.delete test-file))))

;(deftest test-read-friend-xml
;  (is (nil? (read-friend-xml "./test/support_files/fail.xml" test-identity)))
;  (is (nil? (read-friend-xml nil test-identity)))
;  (is (nil? (read-friend-xml test-friend-file nil)))
;  (is (nil? (read-friend-xml nil nil)))
;  (let [friend-id (read-friend-xml test-friend-file test-identity)]
;    (is friend-id)
;    (when friend-id
;      (destroy-record { :id friend-id }))))

;(deftest test-read-friend-xml-string
;  (is (nil? (read-friend-xml "fail" test-identity)))
;  (is (nil? (read-friend-xml "" test-identity)))
;  (is (nil? (read-friend-xml nil test-identity)))
;  (is (nil? (read-friend-xml test-friend-string nil)))
;  (is (nil? (read-friend-xml nil nil)))
;  (let [friend-id (read-friend-xml-string test-friend-string test-identity)]
;    (is friend-id)
;    (when friend-id
;      (destroy-record { :id friend-id }))))

;(deftest test-find-friend
;  (let [friend-id (insert test-friend)
;        inserted-friend (assoc test-friend :id friend-id)]
;    (is friend-id)
;    (try
;      (is (= inserted-friend (find-friend (:name test-name))))
;      (is (= inserted-friend (find-friend inserted-friend)))
;      (is (= inserted-friend (find-friend test-friend)))
;      (is (= inserted-friend (find-friend friend-id)))
;      (try
;        (find-friend 1.0)
;        (is false "Expected an exception for an invalid friend.")
;        (catch Throwable t)) ; Do nothing. this is the expected result.
;      (finally
;        (when friend-id
;          (destroy-record { :id friend-id }))))))

;(deftest test-friend-name
;  (let [friend-id (insert test-friend)
;        inserted-friend (assoc test-friend :id friend-id)]
;    (is friend-id)
;    (try
;      (is (= test-name-2 (friend-name (:name test-name))))
;      (is (= test-name-2 (friend-name inserted-friend)))
;      (is (= test-name-2 (friend-name test-friend)))
;      (is (= test-name-2 (friend-name friend-id)))
;      (try
;        (friend-name 1.0)
;        (is false "Expected an exception for an invalid friend.")
;        (catch Throwable t)) ; Do nothing. this is the expected result.
;      (finally
;        (when friend-id
;          (destroy-record { :id friend-id }))))))

;(deftest test-friend-id
;  (let [friend-record-id (insert test-friend)
;        inserted-friend (assoc test-friend :id friend-record-id)]
;    (is friend-record-id)
;    (try
;      (is (= friend-record-id (friend-id (:name test-name))))
;      (is (= friend-record-id (friend-id inserted-friend)))
;      (is (= friend-record-id (friend-id test-friend)))
;      (is (= friend-record-id (friend-id friend-record-id)))
;      (try
;        (friend-id 1.0)
;        (is false "Expected an exception for an invalid friend.")
;        (catch Throwable t)) ; Do nothing. this is the expected result.
;      (finally
;        (when friend-id
;          (destroy-record { :id friend-record-id }))))))

;(deftest test-friends-in-groups
;  (let [friend-record-id (insert test-friend)
;        inserted-friend (assoc test-friend :id friend-record-id)]
;    (is friend-record-id)
;    (try
;      (let [test-group (first fixtures-group/records)
;            group-membership-id (add-friend-to-group inserted-friend test-group)]
;        (try
;          (is group-membership-id)
;          (is (group-member? inserted-friend test-group))
;          (is (= (group-ids inserted-friend) [(:id test-group)]))
;          (is (= (groups inserted-friend) [test-group]))
;          (remove-friend-from-group inserted-friend test-group)
;          (is (not (group-member? inserted-friend test-group)))
;          (finally
;            (when group-membership-id
;              (group-membership-model/destroy-record { :id group-membership-id })))))
;      (finally
;        (when friend-id
;          (destroy-record { :id friend-record-id }))))))

;(deftest test-has-read-permission?
;  (let [friend-record-id (insert test-friend)
;        inserted-friend (assoc test-friend :id friend-record-id)]
;    (is friend-record-id)
;    (try
;      (let [test-group (first fixtures-group/records)
;            group-membership-id (add-friend-to-group inserted-friend test-group)]
;        (try
;          (is group-membership-id)
;          (is (has-read-permission? inserted-friend (first fixtures-permission/records)))
;          (is (has-write-permission? inserted-friend (first fixtures-permission/records)))
;          (is (not (has-read-permission? inserted-friend (second fixtures-permission/records))))
;          (is (not (has-write-permission? inserted-friend (second fixtures-permission/records))))
;          (finally
;            (when group-membership-id
;              (group-membership-model/destroy-record { :id group-membership-id })))))
;      (finally
;        (when friend-id
;          (destroy-record { :id friend-record-id }))))))