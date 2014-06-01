(ns masques.view.utils.listener-list
  (:require [clojure.tools.logging :as logging]))

(defprotocol ListenerList
  (add-listener [this listener]
    "Adds the given listener to this listener list.")
  
  (remove-listener [this listener]
    "Removes the given listener from this listener list.")
  
  (listeners [this]
    "Returns all of the listeners in this list.")
  
  (count-listeners [this] "Returns the number of listeners in this list."))

(deftype ListenerListImpl [listener-list]
  ListenerList
  (add-listener [this listener]
    (reset! listener-list
            (cons listener @listener-list)))
  
  (remove-listener [this listener]
    (reset! listener-list
            (filter #(not (= listener %)) @listener-list)))
  
  (listeners [this]
    @listener-list)
  
  (count-listeners [this]
    (count @listener-list)))

(defn create
  "Creates a new ListDataListenerList which holds a list of list data listeners."
  []
  (ListenerListImpl. (atom '())))

(defn notify-all-listeners
  "Notifies all of the listeners in the given ListenerList using the
given notifier function which should simply take a listener to update."
  [listener-list notifier]
  (doseq [listener (listeners listener-list)]
    (notifier listener)))