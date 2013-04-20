(ns masques.service.calls.test.profile
  (:require test.init)
  (:use [masques.service.calls.profile])
  (:use [clojure.test])
  (:require [clj-i2p.client :as clj-i2p-client]
            [clj-i2p.core :as clj-i2p]
            [fixtures.friend :as friend-fixture]
            [fixtures.peer :as peer-fixture]
            [masques.service.actions.profile :as profile-action]
            [masques.service.core :as service]
            [masques.test.util :as test-util]))

(def network-destination (atom nil))
(def network-data (atom nil))

(def test-profile { :data 
                    { :name "test" 
                      :email "test@example.com" 
                      :phone-number "123-456-7890" 
                      :address { :address "test address" 
                                 :country "US" 
                                 :province "VA" 
                                 :city "Reston" 
                                 :postal-code "20190" } }})

(defn save-mock-network [destination data]
  (reset! network-destination destination)
  (reset! network-data data)
  test-profile)

;(test-util/use-combined-login-fixture (test-util/create-mock-network-fixture save-mock-network)
;                                      friend-fixture/fixture-map)

(def test-friend (first friend-fixture/records))

;(deftest profile-test
;  (is (nil? @network-destination))
;  (is (nil? @network-data))
;  (is (= (profile test-friend) (:data test-profile)))
;  (is (= (clj-i2p/as-destination-str @network-destination) (:destination (first peer-fixture/records))))
;  (is (= @network-data { :service service/service-name
;                         :data { :action profile-action/action } 
;                         :from { :destination (clj-i2p-client/base-64-destination) } }))) 
