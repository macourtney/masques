(ns fixtures.profile)

(def fixture-table-name :profile)

(def records [
  { :alias "friend"
    :alias_nick nil
    :time_zone nil
    :avatar_file_id nil
    :avatar_nick_file_id nil
    :comments ""
    :identity "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBbntriSYcylfNxTLZNB5tVXHbgSLphQHM++tI0E9wUZnJj7Rh29lgKn4F3W5CXBHFJJDqBJLv2Q5o77pDNlu5H5zAz/AQlzxn/TuFlDYkyBkOv0TUZU3Na7KUqMOBZNr+G1c64VQN4XFkIz5Rh8ki/QhTsNXnvwDOnrMl++FQawIDAQAB"
    :identity_algorithm "RSA"
    :private_key nil
    :private_key_algorithm nil}])

(def fixture-map { :table fixture-table-name :records records })