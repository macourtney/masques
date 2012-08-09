(ns fixtures.permission)

(def fixture-table-name :permissions)

(def records [
  { :id 1
    :name "permission1" }
  { :id 2
    :name "permission2" }
  { :id 3
    :name "permission3" }
  { :id 4
    :name "profile-name" }
  { :id 5
    :name "profile-email" }
  { :id 6
    :name "profile-phone-number" }
  { :id 7
    :name "profile-address" }
  { :id 8
    :name "profile-country" }
  { :id 9
    :name "profile-province" }
  { :id 10
    :name "profile-city" }
  { :id 11
    :name "profile-postal-code" }])

(def fixture-map { :table fixture-table-name :records records })