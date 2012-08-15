(ns masques.controller.main.test.group-tab
  (:require [test.init :as test-init])
  (:require ;[clojure.java.io :as java-io]
            [fixtures.group :as group-fixture]
            ;[masques.model.clipboard :as clipboard-model]
            [masques.model.group :as group-model]
            ;[masques.model.identity :as identity-model]
            [masques.test.util :as test-util]
            [masques.view.main.group-tab :as group-tab-view]
            ;[seesaw.core :as seesaw-core]
            )
  (:use clojure.test
        masques.controller.main.group-tab)
  ;(:import [java.io File StringWriter])
  )

(test-util/use-combined-login-fixture group-fixture/fixture-map)

;(def test-friend-file (File. "test/friend.xml"))

(defn assert-listener-count [test-count]
  (is (= (group-model/group-add-listener-count) test-count)) 
  (is (= (group-model/group-delete-listener-count) test-count)))

(defn assert-no-listeners []
  (assert-listener-count 0))

(defn assert-one-listener-each []
  (assert-listener-count 1))

(defn assert-initialized [frame]
;  (is (= (friend-tab-view/friend-count frame) 1))
;  (is (= (friend-tab-view/friend-xml-text frame) (friend-model/friend-xml-string)))
  (assert-one-listener-each))

;(defn assert-add-remove-friend [frame]
;  (let [friend-identity (identity-model/get-record 3)
;        friend-id (friend-model/add-friend friend-identity)]
;    (is (= (friend-tab-view/friend-count frame) 2))
;    (friend-tab-view/set-selected-friend frame { :id friend-id })
;    (friend-tab-view/click-unfriend-button frame)
;    (is (= (friend-tab-view/friend-count frame) 1))))

;(defn assert-copy [frame]
;  (friend-tab-view/click-copy-text-button frame)
;  (is (= (clipboard-model/retrieve-from-clipboard) (friend-tab-view/friend-xml-text frame))))

;(defn assert-save-file [frame]
;  (is (not (.exists test-friend-file)))
;  (save-friend-xml frame test-friend-file)
;  (is (.exists test-friend-file))
;  (let [string-writer (new StringWriter)]
;    (java-io/copy test-friend-file string-writer)
;    (is (= (.toString string-writer) (friend-tab-view/friend-xml-text frame))))
;  (java-io/delete-file test-friend-file))

(deftest test-show
  (assert-no-listeners)
  (let [frame (test-util/assert-show (group-tab-view/create) init)]
    ;(Thread/sleep 10000)
    (assert-initialized frame)
    ;(assert-add-remove-friend frame)
    ;(assert-copy frame)
    ;(assert-save-file frame)
    ;(Thread/sleep 10000)
    (test-util/assert-close frame)
    (assert-no-listeners)))