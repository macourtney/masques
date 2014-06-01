(ns masques.model.test.message
  (:require test.init)
  (:use clojure.test
        masques.model.base
        masques.model.message))

(deftest test-create-message
  (is (= (count-records message) 0))
  (let [test-message-str "test message"
        test-message (create-message test-message-str)]
    (is (= (count-records message) 1))
    (is test-message)
    (is (= (body test-message) test-message-str))
    (delete-record message test-message)
    (is (= (count-records message) 0))))

(deftest test-create-message
  (let [original-count (count-records message)]
    (let [test-message (create-message "test message")]
      (is (= (count-records message) (inc original-count)))
      (let [test-message-str2 "test message 2"
            updated-message (update-message test-message test-message-str2)]
        (is (= (count-records message) (inc original-count)))
        (is updated-message)
        (is (= (body updated-message) test-message-str2)))
      (delete-record message test-message)
      (is (= (count-records message) original-count)))))

(deftest test-find-or-create
  (is (= (count-records message) 0))
  (let [test-message-str "test message"
        test-message (find-or-create test-message-str)]
    (is (= (count-records message) 1))
    (is test-message)
    (is (= (body test-message) test-message-str))
    (let [test-message2 (find-or-create test-message)]
      (is (= (count-records message) 1))
      (is test-message2)
      (is (= (id test-message2) (id test-message))))
    (delete-record message test-message)
    (is (= (count-records message) 0))))