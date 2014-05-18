(ns masques.view.utils.test.listener-list
  (:use clojure.test
        masques.view.utils.listener-list))

(def called? (atom false))

(defn test-listener []
  (reset! called? true))

(defn test-notifier [listener]
  (listener))

(deftest test-create
  (let [listener-list (create)]
    (is (empty? (listeners listener-list)))
    (add-listener listener-list test-listener)
    (is (= (listeners listener-list) #{test-listener}))
    (is (not @called?))
    (notify-all-listeners listener-list test-notifier)
    (is @called?)
    (remove-listener listener-list test-listener)
    (is (empty? (listeners listener-list)))))