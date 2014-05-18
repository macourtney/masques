(ns masques.model.test.base
  (:require test.init)
  (:use clojure.test
        masques.model.base))

(deftest test-listener-set-for-entity
  (is (= (listener-set-for-entity {} friend-request) #{}))
  (let [test-listener-set #{ :foo }]
    (is (= (listener-set-for-entity
             { friend-request test-listener-set } friend-request)
           test-listener-set))))

(deftest test-add-listener-to-listeners-map
  (let [test-listener (fn [_])
        updated-listeners-map
          (add-listener-to-listeners-map {} friend-request test-listener)]
    (is (= updated-listeners-map { friend-request #{ test-listener } })))
  (let [test-listener (fn [_])
        test-listener2 (fn [_])
        updated-listeners-map (add-listener-to-listeners-map
                                { profile #{ test-listener2 } } 
                                friend-request test-listener)]
    (is (= updated-listeners-map
           { friend-request #{ test-listener }
             profile #{ test-listener2 } })))
  (let [test-listener (fn [_])
        test-listener2 (fn [_])
        updated-listeners-map (add-listener-to-listeners-map
                                { friend-request #{ test-listener2 } } 
                                friend-request test-listener)]
    (is (= updated-listeners-map
           { friend-request #{ test-listener test-listener2 } })))
  (let [updated-listeners-map
          (add-listener-to-listeners-map {} friend-request nil)]
    (is (= updated-listeners-map {})))
  (let [test-listener (fn [_])
        updated-listeners-map
          (add-listener-to-listeners-map {} nil test-listener)]
    (is (= updated-listeners-map {})))
  (let [test-listener (fn [_])
        updated-listeners-map
          (add-listener-to-listeners-map nil friend-request test-listener)]
    (is (= updated-listeners-map { friend-request #{ test-listener } }))))

(deftest test-remove-listener-from-listeners-map
  (let [test-listener (fn [_])
        updated-listeners-map
          (remove-listener-from-listeners-map
            { friend-request #{ test-listener } } friend-request test-listener)]
    (is (= updated-listeners-map {})))
  (let [test-listener (fn [_])
        test-listener2 (fn [_])
        updated-listeners-map
          (remove-listener-from-listeners-map
            { friend-request #{ test-listener test-listener2 } } friend-request
            test-listener)]
    (is (= updated-listeners-map { friend-request #{ test-listener2 } })))
  (let [test-listener (fn [_])
        test-listener2 (fn [_])
        updated-listeners-map
          (remove-listener-from-listeners-map
            { friend-request #{ test-listener2 } } friend-request
            test-listener)]
    (is (= updated-listeners-map { friend-request #{ test-listener2 } })))
  (let [test-listener (fn [_])
        test-listener2 (fn [_])
        updated-listeners-map
          (remove-listener-from-listeners-map
            { profile #{ test-listener2 } } friend-request test-listener)]
    (is (= updated-listeners-map { profile #{ test-listener2 } })))
  (let [test-listener (fn [_])
        updated-listeners-map
          (remove-listener-from-listeners-map {} friend-request test-listener)]
    (is (= updated-listeners-map {}))))

(deftest test-add-listener
  (let [test-listener (fn [_])
        listener-atom (atom {})]
    (add-listener listener-atom friend-request test-listener)
    (is (= @listener-atom { friend-request #{ test-listener } })))
  (let [test-listener (fn [_])
        test-listener2 (fn [_])
        listener-atom (atom { profile #{ test-listener2 } })]
    (add-listener listener-atom friend-request test-listener)
    (is (= @listener-atom
           { friend-request #{ test-listener }
             profile #{ test-listener2 } })))
  (let [test-listener (fn [_])
        test-listener2 (fn [_])
        listener-atom (atom { friend-request #{ test-listener2 } })]
    (add-listener listener-atom friend-request test-listener)
    (is (= @listener-atom
           { friend-request #{ test-listener test-listener2 } })))
  (let [listener-atom (atom {})]
    (add-listener listener-atom friend-request nil)
    (is (= @listener-atom {})))
  (let [listener-atom (atom {})
        test-listener (fn [_])]
    (add-listener listener-atom nil test-listener)
    (is (= @listener-atom {}))))

(deftest test-remove-listener
  (let [test-listener (fn [_])
        listener-atom (atom { friend-request #{ test-listener } })]
    (remove-listener listener-atom friend-request test-listener)
    (is (= @listener-atom {})))
  (let [test-listener (fn [_])
        test-listener2 (fn [_])
        listener-atom (atom { friend-request 
                                #{ test-listener test-listener2 } })]
    (remove-listener listener-atom friend-request test-listener)
    (is (= @listener-atom { friend-request #{ test-listener2 } })))
  (let [test-listener (fn [_])
        test-listener2 (fn [_])
        listener-atom (atom { friend-request #{ test-listener2 } })]
    (remove-listener listener-atom friend-request test-listener)
    (is (= @listener-atom { friend-request #{ test-listener2 } })))
  (let [test-listener (fn [_])
        test-listener2 (fn [_])
        listener-atom (atom { profile #{ test-listener2 } })]
    (remove-listener listener-atom friend-request test-listener)
    (is (= @listener-atom { profile #{ test-listener2 } })))
  (let [test-listener (fn [_])
        listener-atom (atom {})]
    (remove-listener listener-atom friend-request test-listener)
    (is (= @listener-atom {}))))

(deftest test-notify-listeners
  (let [test-id 12345]
    (let [notify-atom (atom nil)
          test-listener (fn [id] (reset! notify-atom id))
          listener-atom (atom {})]
      (add-listener listener-atom friend-request test-listener)
      (notify-listeners listener-atom friend-request test-id)
      (is (= @notify-atom test-id)))
    (notify-listeners (atom { friend-request #{} }) friend-request test-id)
    (notify-listeners (atom {}) friend-request test-id)))

(deftest test-change-listener
  (let [test-listener (fn [_])]
    (is (= {} @change-listeners))
    (add-change-listener friend-request test-listener)
    (is (= { friend-request #{ test-listener } } @change-listeners))
    (remove-change-listener friend-request test-listener)
    (is (= {} @change-listeners)))
  (add-change-listener friend-request nil)
  (is (= {} @change-listeners))
  (remove-change-listener friend-request nil)
  (is (= {} @change-listeners)))

(deftest test-insert-listener
  (let [test-id 12345
        insert-notify-atom (atom nil)
        test-insert-listener (fn [id] (reset! insert-notify-atom id))
        change-notify-atom (atom nil)
        test-change-listener (fn [id] (reset! change-notify-atom id))]
    (is (= {} @insert-listeners))
    (is (= {} @change-listeners))
    (add-insert-listener friend-request test-insert-listener)
    (is (= { friend-request #{ test-insert-listener } } @insert-listeners))
    (add-change-listener friend-request test-change-listener)
    (notify-of-insert friend-request test-id)
    (is (= test-id @insert-notify-atom))
    (is (= test-id @change-notify-atom))
    (remove-change-listener friend-request test-change-listener)
    (remove-insert-listener friend-request test-insert-listener)
    (is (= {} @insert-listeners))
    (is (= {} @change-listeners)))
  (add-insert-listener friend-request nil)
  (is (= {} @insert-listeners))
  (remove-insert-listener friend-request nil)
  (is (= {} @insert-listeners)))

(deftest test-insert-listener
  (let [test-id 12345
        update-notify-atom (atom nil)
        test-update-listener (fn [id] (reset! update-notify-atom id))
        change-notify-atom (atom nil)
        test-change-listener (fn [id] (reset! change-notify-atom id))]
    (is (= {} @update-listeners))
    (is (= {} @change-listeners))
    (add-update-listener friend-request test-update-listener)
    (is (= { friend-request #{ test-update-listener } } @update-listeners))
    (add-change-listener friend-request test-change-listener)
    (notify-of-update friend-request test-id)
    (is (= test-id @update-notify-atom))
    (is (= test-id @change-notify-atom))
    (remove-change-listener friend-request test-change-listener)
    (remove-update-listener friend-request test-update-listener)
    (is (= {} @update-listeners))
    (is (= {} @change-listeners)))
  (add-update-listener friend-request nil)
  (is (= {} @update-listeners))
  (remove-update-listener friend-request nil)
  (is (= {} @update-listeners)))

(deftest test-delete-listener
  (let [test-id 12345
        delete-notify-atom (atom nil)
        test-delete-listener (fn [id] (reset! delete-notify-atom id))
        change-notify-atom (atom nil)
        test-change-listener (fn [id] (reset! change-notify-atom id))]
    (is (= {} @delete-listeners))
    (is (= {} @change-listeners))
    (add-delete-listener friend-request test-delete-listener)
    (is (= { friend-request #{ test-delete-listener } } @delete-listeners))
    (add-change-listener friend-request test-change-listener)
    (notify-of-delete friend-request test-id)
    (is (= test-id @delete-notify-atom))
    (is (= test-id @change-notify-atom))
    (remove-change-listener friend-request test-change-listener)
    (remove-delete-listener friend-request test-delete-listener)
    (is (= {} @delete-listeners))
    (is (= {} @change-listeners)))
  (add-delete-listener friend-request nil)
  (is (= {} @delete-listeners))
  (remove-delete-listener friend-request nil)
  (is (= {} @delete-listeners)))