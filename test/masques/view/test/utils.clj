(ns masques.view.test.utils
  (:use clojure.test
        masques.view.utils)
  (:import [javax.swing JPanel]))

(deftest test-component-property
  (let [test-key :key
        test-value :value
        test-panel (new JPanel)]
    (save-component-property test-panel test-key test-value)
    (is (= (retrieve-component-property test-panel test-key) test-value))
    (is (= (remove-component-property test-panel test-key) test-value))
    (is (nil? (retrieve-component-property test-panel test-key)))))