(ns masques.model.grouping
  (:use masques.model.base
        korma.core))

(defn save [record]
  (insert-or-update grouping record))

(defn get-profiles [grouping-id]
  (into [] (select grouping-profile (where {:GROUPING_ID grouping-id}))))

(defn attach-profiles [album-record file-records]
  (assoc album-record :files file-records))

(defn with-profiles [id]
  (let [grouping-record (find-by-id grouping id)]
	(attach-profiles grouping-record (get-profiles id))))