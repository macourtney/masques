(ns masques.controller.data-directory.test.choose
  (:use clojure.test
        masques.controller.data-directory.choose))

(deftest test-show
  (let [frame (show)]
    (is frame)
    (is (.isShowing frame))
    (Thread/sleep 10000)
    (.setVisible frame false)
    (.dispose frame)
    (is (not (.isShowing frame)))))