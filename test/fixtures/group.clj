(ns fixtures.group)

(def fixture-table-name :groups)

(def records [
  { :id 1
    :name "friends"
    :identity_id 1
    :user_generated 1 }
  { :id 2
    :name "enemies"
    :identity_id 1
    :user_generated 0 }
  { :id 3
    :name "group3"
    :identity_id 2
    :user_generated 1 }])

(def fixture-map { :table fixture-table-name :records records })