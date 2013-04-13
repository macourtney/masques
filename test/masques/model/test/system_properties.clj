(ns masques.model.test.system-properties
  (:use clojure.test
        masques.model.system-properties))

(deftest test-edit-datadir
  (let [old-data-dir (read-data-directory)
        test-dir "testDir"]
    ;(delete-data-directory)
    (is (not (read-data-directory)))
    (set-data-directory test-dir)
    (is (= (read-data-directory) test-dir))
    (delete-data-directory)
    (is (not (read-data-directory)))
    (set-data-directory old-data-dir)))