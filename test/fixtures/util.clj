(ns fixtures.util
  (:refer-clojure :exclude [boolean byte-array])
  (:require [clojure.test :as clojure-test]
            [masques.model.base :as model-base]
            [test.init :as test-init])
  (:use drift-db.core))

(defn load-records [fixture-map]
  (apply insert-into
         (:table fixture-map)
         (map model-base/clean-up-for-h2 (:records fixture-map))))

(defn unload-records [fixture-map]
  (try
    (delete (:table fixture-map) ["true"])
    (catch Throwable t
      (println "An error occured while trying to empty table:" (:table fixture-map))))) 

(defn run-fixture [fixture-map function]
  (try
    (load-records fixture-map) 
    (function)
    (finally
      (unload-records fixture-map))))

(defn build-table-map
  "Collects the given fixture and all of the fixtures it depends on into a single map keyed off the table the fixture
updates."
  [table-map fixture-maps]
  (if-let [fixture-map (first fixture-maps)]
    (let [new-table-map (assoc table-map (:table fixture-map) fixture-map)]
      (recur new-table-map (concat (rest fixture-maps)
                                             (filter #(not (contains? new-table-map (:table %1)))
                                               (:required-fixtures fixture-map)))))
    table-map)) 

(defn identity-fixture
  "Simply runs the fixture given to it and does nothing else."
  [function]
  (function))

(defn create-fixture-fn
  "Converts the given fixture map into a fixture function."
  [fixture-map]
  (partial run-fixture fixture-map))

(defn create-fixture
  "Returns a fixture combining all of the fixtures created from the given fixture maps."
  [fixture-maps]
  (if (and fixture-maps (not-empty fixture-maps))
    (clojure-test/join-fixtures (map create-fixture-fn (vals (build-table-map {} fixture-maps))))
    identity-fixture))

(defn use-fixture-maps [fixture-type & fixture-maps]
  (clojure-test/use-fixtures fixture-type (create-fixture fixture-maps)))