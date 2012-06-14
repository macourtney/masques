(ns masques.model.test.peer
  (:require [fixtures.peer :as fixtures-peer]) 
  (:use clojure.test
        masques.model.peer))

(def test-peer (first fixtures-peer/records))

(deftest test-xml
  (let [peer-xml (xml test-peer)]
    (is (= (count (:attrs peer-xml)) 1))
    (is (= (:destination test-peer) (:destination (:attrs peer-xml))))))