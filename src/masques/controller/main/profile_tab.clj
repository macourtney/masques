(ns masques.controller.main.profile-tab
  (:require [clojure.tools.logging :as logging]
            [masques.model.address :as address-model]
            [masques.model.email-address :as email-model]
            [masques.model.name :as name-model]
            [masques.model.phone-number :as phone-number-model]
            [seesaw.core :as seesaw-core]))

(defn find-name-text [main-frame]
  (seesaw-core/select main-frame ["#name-text"]))

(defn find-email-text [main-frame]
  (seesaw-core/select main-frame ["#email-text"]))

(defn find-phone-number-text [main-frame]
  (seesaw-core/select main-frame ["#phone-number-text"]))

(defn find-address-text [main-frame]
  (seesaw-core/select main-frame ["#address-text"]))

(defn set-name [main-frame name]
  (.setText (find-name-text main-frame) name)
  main-frame)

(defn set-email [main-frame email]
  (.setText (find-email-text main-frame) email)
  main-frame)

(defn set-phone-number [main-frame phone-number]
  (.setText (find-phone-number-text main-frame) phone-number)
  main-frame)

(defn set-address [main-frame address]
  (.setText (find-address-text main-frame) address)
  main-frame)

(defn load-name [main-frame]
  (set-name main-frame (:name (name-model/first-current-identity-name))))

(defn load-email [main-frame]
  (set-email main-frame (:email-address (email-model/first-current-identity-email-address))))

(defn load-phone-number [main-frame]
  (set-phone-number main-frame (:phone-number (phone-number-model/first-current-identity-phone-number))))

(defn load-address [main-frame]
  (set-address main-frame (:address (address-model/first-current-identity-address))))

(defn load-data [main-frame]
  (load-address (load-phone-number (load-email (load-name main-frame)))))

(defn attach [main-frame]
  main-frame)

(defn init [main-frame]
  (attach (load-data main-frame)))