(ns masques.controller.friend.test.view
  (:refer-clojure :exclude [key load])
  (:require [test.init :as test-init])
  (:require [clojure.java.io :as java-io]
            [fixtures.friend :as friend-fixture]
            [masques.model.clipboard :as clipboard-model]
            [masques.model.friend :as friend-model]
            [masques.test.util :as test-util]
            [masques.view.friend.add :as add-friend-view]
            [seesaw.core :as seesaw-core])
  (:use clojure.test
        masques.controller.friend.view)
  (:import [java.io File]))

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
  test-profile)

;(test-util/use-combined-login-fixture (test-util/create-mock-network-fixture save-mock-network)
;                                      friend-fixture/fixture-map)

(def test-friend (first friend-fixture/records))

;(deftest test-create 
;  (let [frame (show nil test-friend)]
;    (is frame)
;    (is (.isShowing frame))
;    (is (= (scrape-profile frame) (:data test-profile)))
;    (click-done frame)
;    (is (not (.isShowing frame)))))
