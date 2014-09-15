(ns masques.model.grouping
  (:require [clj-internationalization.term :as term]
            [clojure.tools.logging :as logging])
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

(defn all-grouping
  "Returns all of the groupings."
  []
  (select grouping (fields id-key)))

(defn save [record]
  (insert-or-update grouping record))

(defn delete-grouping
  "Deletes the given grouping from the database."
  [record]
  (delete-record grouping record))

(defn display
  "Returns the display value for the given grouping."
  [grouping]
  (display-key grouping))

(defn read-name
  "Returns the name of the given grouping."
  [grouping]
  (name-key grouping))

(defn create-user-group
  "Creates a new group marked as generated by the user. The given group name is
used for both the display and name of the newly created group."
  [group-name]
  (save { display-key group-name
          name-key group-name
          user-generated-key true }))

(defn profiles [grouping-id]
  (into [] (select grouping-profile (where {:GROUPING_ID (id grouping-id)}))))

(defn add-grouping-delete-interceptor
  "Adds the given group delete interceptor."
  [interceptor]
  (add-delete-interceptor grouping interceptor))

(defn remove-grouping-delete-interceptor
  "Removes the given group delete interceptor."
  [interceptor]
  (remove-delete-interceptor grouping interceptor))

(defn add-grouping-insert-interceptor
  "Adds the given group insert interceptor."
  [interceptor]
  (add-insert-interceptor grouping interceptor))

(defn remove-grouping-insert-interceptor
  "Removes the given group insert interceptor."
  [interceptor]
  (remove-insert-interceptor grouping interceptor))

(defn add-grouping-update-interceptor
  "Adds the given group update interceptor."
  [interceptor]
  (add-update-interceptor grouping interceptor))

(defn remove-grouping-update-interceptor
  "Removes the given group update interceptor."
  [interceptor]
  (remove-update-interceptor grouping interceptor))

(defn count-groups
  "Counts all of the groups."
  []
  (count-records grouping))

(defn find-combobox-group
  "Returns a groups with just the id and name at the given index."
  [index]
  (first
    (select
      grouping
      (fields id-key (h2-keyword display-key))
      (limit 1)
      (offset index)
      (order (h2-keyword display-key) :ASC))))

(defn combobox-index-of
  "Returns the index of the given grouping record in the list of combobox
groups."
  [record]
  (index-of
    record
    (select
      grouping
      (fields id-key)
      (order (h2-keyword display-key) :ASC))))

(defn find-id-by-name
  "Returns the id of the group with the given name."
  [name]
  (id
    (first
      (select
        grouping
        (fields id-key)
        (where { (h2-keyword name-key) name })
        (limit 1)))))

(defn find-everyone-id
  "Returns the id for the everyone group."
  []
  (find-id-by-name everyone-name))

(defn find-friends-id
  "Returns the id for the friends group."
  []
  (find-id-by-name friends-name))

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

(defn contains-any-profile?
  "Returns true if the given grouping has any profiles in it. Currently
unimplemented."
  [grouping]
  false)