(ns masques.view.subviews.test.profile-data
  (:use clojure.test
        masques.view.subviews.profile-data))

(def name "blah")
(def email "blah@example.com")
(def phone-number "123-456-7890")
(def full-address { :address "2342 main st."
                    :country "US"
                    :province "VA"
                    :city "Reston"
                    :postal-code "20190" })

(deftest test-create
  (let [profile-panel (create-profile-panel)]
    (is profile-panel)
    (set-data profile-panel name email phone-number full-address)
    (is (= (scrape-data profile-panel)
           { :name name
             :email email
             :phone-number phone-number
             :address full-address }))))