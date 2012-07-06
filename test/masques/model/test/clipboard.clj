(ns masques.model.test.clipboard
  (:use clojure.test
        masques.model.clipboard))

(deftest test-clipboard
  (let [test-text "blah blah blah"]
    (save-to-clipboard! test-text)
    (is (= (retrieve-from-clipboard) test-text))))