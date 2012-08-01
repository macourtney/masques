(ns fixtures.permission)

(def fixture-table-name :permissions)

(def records [
  { :id 1
    :name "permission1"
    :identity_id 2 }
  { :id 2
    :name "permission2"
    :identity_id 2 }
  { :id 3
    :name "permission3"
    :identity_id 1 }])

(def fixture-map { :table fixture-table-name :records records })