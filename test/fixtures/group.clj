(ns fixtures.group)

(def fixture-table-name :groups)

(def records [
  { :id 1
    :name "group1"
    :identity_id 2 }
  { :id 2
    :name "group2"
    :identity_id 2 }
  { :id 3
    :name "group3"
    :identity_id 1 }])

(def fixture-map { :table fixture-table-name :records records })