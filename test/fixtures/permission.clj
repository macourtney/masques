(ns fixtures.permission)

(def fixture-table-name :permissions)

(def records [
  { :id 1
    :name "permission1" }
  { :id 2
    :name "permission2" }
  { :id 3
    :name "permission3" }])

(def fixture-map { :table fixture-table-name :records records })