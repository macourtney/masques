(ns fixtures.group)

(def fixture-table-name :groups)

(def records [
  { :id 1
    :name "group1" }
  { :id 2
    :name "group1" }])

(def fixture-map { :table fixture-table-name :records records })