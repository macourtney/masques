(ns masques.model.test.record-utils
  (:use clojure.test
        masques.model.record-utils))

(deftest test-clean-key
  (is (= (clean-key :test_key) :test-key))
  (is (= (clean-key :test-key) :test-key))
  (is (= (clean-key "test_key") :test-key))
  (is (= (clean-key 'test_key) :test-key))
  (is (nil? (clean-key nil))))

(deftest test-clean-keys
  (is (= (clean-keys { :test_key "blah" }) { :test-key "blah" }))
  (is (= (clean-keys { :test-key "blah" }) { :test-key "blah" }))
  (is (= (clean-keys { "test_key" "blah" }) { :test-key "blah" }))
  (is (= (clean-keys { 'test_key "blah" }) { :test-key "blah" }))
  (is (= (clean-keys {}) {}))
  (is (nil? (clean-keys nil))))