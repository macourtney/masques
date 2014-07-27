(ns masques.model.test.grouping
  (:require test.init)
  (:use clojure.test
        masques.model.base
        masques.model.grouping)
  (:require [korma.core :as korma]))


(def grouping-map {name-key "tester grouping"})

(deftest test-add-grouping
  (let [grouping-record (find-grouping (save grouping-map))]
    (is grouping-record)
    (is (id grouping-record))
    (is (= (name-key grouping-record) "tester grouping"))
    (is (instance? org.joda.time.DateTime (created-at-key grouping-record)))
    (delete-grouping grouping-record)))

(deftest test-init
  (when-let [groupings (all-grouping)]
    (doseq [grouping groupings]
      (delete-grouping grouping)))
  (is (= 0 (count-groups)))
  (init)
  (is (= 5 (count-groups)))
  (doseq [group (korma/select grouping)]
    (delete-grouping group)))

(deftest test-combobox-index-of
  (let [grouping-record (save grouping-map)]
    (is (= (combobox-index-of grouping-record) 0))
    (delete-grouping grouping-record)))