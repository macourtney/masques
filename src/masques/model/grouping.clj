(ns masques.model.grouping
  (:require [clj-internationalization.term :as term]
            [korma.core :as korma])
  (:use masques.model.base
        korma.core))

(def created-at-key :created-at)
(def display-key :display)
(def name-key :name)
(def user-generated-key :user-generated)

(def everyone-name "everyone")
(def friends-name "friends")
(def family-name "family")
(def acquaintances-name "acquaintances")
(def companies-name "companies")

(defn find-grouping
  "Returns the grouping with the given id."
  [grouping-id]
  (when grouping-id
    (find-by-id grouping (id grouping-id))))

(defn save [record]
  (insert-or-update grouping record))

(defn get-profiles [grouping-id]
  (into [] (select grouping-profile (where {:GROUPING_ID grouping-id}))))

(defn attach-profiles [album-record file-records]
  (assoc album-record :files file-records))

(defn with-profiles [id]
  (let [grouping-record (find-by-id grouping id)]
	(attach-profiles grouping-record (get-profiles id))))

(defn count-groups
  "Counts all of the groups."
  []
  (:count
    (first
      (korma/select
        grouping
        (korma/aggregate (count :*) :count)))))

(defn find-combobox-group
  "Returns a groups with just the id and name at the given index."
  [index]
  (first
    (korma/select
      grouping
      (korma/fields id-key (h2-keyword display-key))
      (korma/limit 1)
      (korma/offset index))))

(defn find-everyone-id
  "Returns the id for the everyone group."
  []
  (id
    (first
      (korma/select
        grouping
        (korma/fields id-key)
        (korma/where { (h2-keyword name-key) everyone-name })
        (korma/limit 1)))))

(defn init
  "Initializes the grouping. If no groupings exists then this function adds all
of the default groups."
  []
  (when (= 0 (count-groups))
    (save
      { display-key (term/everyone)
        name-key everyone-name
        user-generated-key false })
    (save
      { display-key (term/friends)
        name-key friends-name
        user-generated-key false })
    (save
      { display-key (term/family)
        name-key family-name
        user-generated-key false })
    (save
      { display-key (term/acquaintances)
        name-key acquaintances-name
        user-generated-key false })
    (save
      { display-key (term/companies)
        name-key companies-name
        user-generated-key false })))