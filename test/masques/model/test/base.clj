(ns masques.model.test.base
  (:require test.init)
  (:use clojure.test
        masques.model.base))

(deftest test-interceptor-set-for-entity
  (is (= (interceptor-set-for-entity {} friend-request) #{}))
  (let [test-interceptor-set #{ :foo }]
    (is (= (interceptor-set-for-entity
             { friend-request test-interceptor-set } friend-request)
           test-interceptor-set))))

(deftest test-add-interceptor-to-interceptors-map
  (let [test-interceptor (fn [action record] (action record))
        updated-interceptors-map (add-interceptor-to-interceptors-map
                                   {} friend-request test-interceptor)]
    (is (= updated-interceptors-map { friend-request #{ test-interceptor } })))
  (let [test-interceptor (fn [action record] (action record))
        test-interceptor2 (fn [action record] (action record))
        updated-interceptors-map (add-interceptor-to-interceptors-map
                                   { profile #{ test-interceptor2 } } 
                                   friend-request test-interceptor)]
    (is (= updated-interceptors-map
           { friend-request #{ test-interceptor }
             profile #{ test-interceptor2 } })))
  (let [test-interceptor (fn [action record] (action record))
        test-interceptor2 (fn [action record] (action record))
        updated-interceptors-map (add-interceptor-to-interceptors-map
                                   { friend-request #{ test-interceptor2 } } 
                                   friend-request test-interceptor)]
    (is (= updated-interceptors-map
           { friend-request #{ test-interceptor test-interceptor2 } })))
  (let [updated-interceptors-map
          (add-interceptor-to-interceptors-map {} friend-request nil)]
    (is (= updated-interceptors-map {})))
  (let [test-interceptor (fn [action record] (action record))
        updated-interceptors-map
          (add-interceptor-to-interceptors-map {} nil test-interceptor)]
    (is (= updated-interceptors-map {})))
  (let [test-interceptor (fn [action record] (action record))
        updated-interceptors-map (add-interceptor-to-interceptors-map
                                   nil friend-request test-interceptor)]
    (is (= updated-interceptors-map { friend-request #{ test-interceptor } }))))

(deftest test-remove-interceptor-from-interceptors-map
  (let [test-interceptor (fn [action record] (action record))
        updated-interceptors-map
          (remove-interceptor-from-interceptors-map
            { friend-request #{ test-interceptor } }
            friend-request test-interceptor)]
    (is (= updated-interceptors-map {})))
  (let [test-interceptor (fn [action record] (action record))
        test-interceptor2 (fn [action record] (action record))
        updated-interceptors-map
          (remove-interceptor-from-interceptors-map
            { friend-request #{ test-interceptor test-interceptor2 } }
            friend-request test-interceptor)]
    (is (= updated-interceptors-map { friend-request #{ test-interceptor2 } })))
  (let [test-interceptor (fn [action record] (action record))
        test-interceptor2 (fn [action record] (action record))
        updated-interceptors-map
          (remove-interceptor-from-interceptors-map
            { friend-request #{ test-interceptor2 } } friend-request
            test-interceptor)]
    (is (= updated-interceptors-map { friend-request #{ test-interceptor2 } })))
  (let [test-interceptor (fn [action record] (action record))
        test-interceptor2 (fn [action record] (action record))
        updated-interceptors-map
          (remove-interceptor-from-interceptors-map
            { profile #{ test-interceptor2 } } friend-request test-interceptor)]
    (is (= updated-interceptors-map { profile #{ test-interceptor2 } })))
  (let [test-interceptor (fn [action record] (action record))
        updated-interceptors-map (remove-interceptor-from-interceptors-map
                                   {} friend-request test-interceptor)]
    (is (= updated-interceptors-map {}))))

(deftest test-add-interceptor
  (let [test-interceptor (fn [action record] (action record))
        interceptor-atom (atom {})]
    (add-interceptor interceptor-atom friend-request test-interceptor)
    (is (= @interceptor-atom { friend-request #{ test-interceptor } })))
  (let [test-interceptor (fn [action record] (action record))
        test-interceptor2 (fn [action record] (action record))
        interceptor-atom (atom { profile #{ test-interceptor2 } })]
    (add-interceptor interceptor-atom friend-request test-interceptor)
    (is (= @interceptor-atom
           { friend-request #{ test-interceptor }
             profile #{ test-interceptor2 } })))
  (let [test-interceptor (fn [action record] (action record))
        test-interceptor2 (fn [action record] (action record))
        interceptor-atom (atom { friend-request #{ test-interceptor2 } })]
    (add-interceptor interceptor-atom friend-request test-interceptor)
    (is (= @interceptor-atom
           { friend-request #{ test-interceptor test-interceptor2 } })))
  (let [interceptor-atom (atom {})]
    (add-interceptor interceptor-atom friend-request nil)
    (is (= @interceptor-atom {})))
  (let [interceptor-atom (atom {})
        test-interceptor (fn [action record] (action record))]
    (add-interceptor interceptor-atom nil test-interceptor)
    (is (= @interceptor-atom {}))))

(deftest test-remove-interceptor
  (let [test-interceptor (fn [action record] (action record))
        interceptor-atom (atom { friend-request #{ test-interceptor } })]
    (remove-interceptor interceptor-atom friend-request test-interceptor)
    (is (= @interceptor-atom {})))
  (let [test-interceptor (fn [action record] (action record))
        test-interceptor2 (fn [action record] (action record))
        interceptor-atom (atom { friend-request 
                                #{ test-interceptor test-interceptor2 } })]
    (remove-interceptor interceptor-atom friend-request test-interceptor)
    (is (= @interceptor-atom { friend-request #{ test-interceptor2 } })))
  (let [test-interceptor (fn [action record] (action record))
        test-interceptor2 (fn [action record] (action record))
        interceptor-atom (atom { friend-request #{ test-interceptor2 } })]
    (remove-interceptor interceptor-atom friend-request test-interceptor)
    (is (= @interceptor-atom { friend-request #{ test-interceptor2 } })))
  (let [test-interceptor (fn [action record] (action record))
        test-interceptor2 (fn [action record] (action record))
        interceptor-atom (atom { profile #{ test-interceptor2 } })]
    (remove-interceptor interceptor-atom friend-request test-interceptor)
    (is (= @interceptor-atom { profile #{ test-interceptor2 } })))
  (let [test-interceptor (fn [action record] (action record))
        interceptor-atom (atom {})]
    (remove-interceptor interceptor-atom friend-request test-interceptor)
    (is (= @interceptor-atom {}))))

(deftest test-call-interceptors
  (let [test-record { id-key 12345 }]
    (let [notify-atom (atom nil)
          test-interceptor (fn [action record]
                          (reset! notify-atom record)
                          (action record))]
      (call-interceptors [test-interceptor] identity test-record)
      (is (= @notify-atom test-record)))
    (call-interceptors [] identity test-record)))

(deftest test-change-interceptor
  (let [test-interceptor (fn [action record] (action record))]
    (is (= {} @change-interceptors))
    (add-change-interceptor friend-request test-interceptor)
    (is (= { friend-request #{ test-interceptor } } @change-interceptors))
    (remove-change-interceptor friend-request test-interceptor)
    (is (= {} @change-interceptors)))
  (add-change-interceptor friend-request nil)
  (is (= {} @change-interceptors))
  (remove-change-interceptor friend-request nil)
  (is (= {} @change-interceptors)))

(deftest test-insert-interceptor
  (let [test-record { id-key 12345 }
        insert-notify-atom (atom nil)
        test-insert-interceptor (fn [action record]
                                  (reset! insert-notify-atom id)
                                  (action record))
        change-notify-atom (atom nil)
        test-change-interceptor (fn [action record]
                                  (reset! change-notify-atom id)
                                  (action record))]
    (is (= {} @insert-interceptors))
    (is (= {} @change-interceptors))
    (add-insert-interceptor friend-request test-insert-interceptor)
    (is (= { friend-request #{ test-insert-interceptor } }
           @insert-interceptors))
    (add-change-interceptor friend-request test-change-interceptor)
    (run-insert friend-request identity test-record)
    (is (= test-record @insert-notify-atom))
    (is (= test-record @change-notify-atom))
    (remove-change-interceptor friend-request test-change-interceptor)
    (remove-insert-interceptor friend-request test-insert-interceptor)
    (is (= {} @insert-interceptors))
    (is (= {} @change-interceptors)))
  (add-insert-interceptor friend-request nil)
  (is (= {} @insert-interceptors))
  (remove-insert-interceptor friend-request nil)
  (is (= {} @insert-interceptors)))

(deftest test-insert-interceptor
  (let [test-record { id-key 12345 }
        update-notify-atom (atom nil)
        test-update-interceptor (fn [action record]
                                  (reset! update-notify-atom record)
                                  (action record))
        change-notify-atom (atom nil)
        test-change-interceptor (fn [action record]
                                  (reset! change-notify-atom record)
                                  (action record))]
    (is (= {} @update-interceptors))
    (is (= {} @change-interceptors))
    (add-update-interceptor friend-request test-update-interceptor)
    (is (= { friend-request #{ test-update-interceptor } }
           @update-interceptors))
    (add-change-interceptor friend-request test-change-interceptor)
    (run-update friend-request identity test-record)
    (is (= test-record @update-notify-atom))
    (is (= test-record @change-notify-atom))
    (remove-change-interceptor friend-request test-change-interceptor)
    (remove-update-interceptor friend-request test-update-interceptor)
    (is (= {} @update-interceptors))
    (is (= {} @change-interceptors)))
  (add-update-interceptor friend-request nil)
  (is (= {} @update-interceptors))
  (remove-update-interceptor friend-request nil)
  (is (= {} @update-interceptors)))

(deftest test-delete-interceptor
  (let [test-record { id-key 12345 }
        delete-notify-atom (atom nil)
        test-delete-interceptor (fn [action record]
                                  (reset! delete-notify-atom record)
                                  (action record))
        change-notify-atom (atom nil)
        test-change-interceptor (fn [action record]
                                  (reset! change-notify-atom record)
                                  (action record))]
    (is (= {} @delete-interceptors))
    (is (= {} @change-interceptors))
    (add-delete-interceptor friend-request test-delete-interceptor)
    (is (= { friend-request #{ test-delete-interceptor } }
           @delete-interceptors))
    (add-change-interceptor friend-request test-change-interceptor)
    (run-delete friend-request identity test-record)
    (is (= test-record @delete-notify-atom))
    (is (= test-record @change-notify-atom))
    (remove-change-interceptor friend-request test-change-interceptor)
    (remove-delete-interceptor friend-request test-delete-interceptor)
    (is (= {} @delete-interceptors))
    (is (= {} @change-interceptors)))
  (add-delete-interceptor friend-request nil)
  (is (= {} @delete-interceptors))
  (remove-delete-interceptor friend-request nil)
  (is (= {} @delete-interceptors)))

(deftest test-index-of
  (let [test-record { :id 1 }
        test-record-2 { :id 2 }]
    (is (nil? (index-of test-record [])))
    (is (= (index-of test-record [test-record]) 0))
    (is (= (index-of test-record [test-record-2 test-record]) 1))
    (is (nil? (index-of test-record [test-record-2])))))