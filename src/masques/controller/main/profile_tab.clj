(ns masques.controller.main.profile-tab
  (:require [clojure.tools.logging :as logging]
            [masques.controller.actions.utils :as action-utils]
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

(defn find-country-text [main-frame]
  (seesaw-core/select main-frame ["#country-text"]))

(defn find-province-text [main-frame]
  (seesaw-core/select main-frame ["#province-text"]))

(defn find-city-text [main-frame]
  (seesaw-core/select main-frame ["#city-text"]))

(defn find-postal-code-text [main-frame]
  (seesaw-core/select main-frame ["#postal-code-text"]))

(defn set-name [main-frame name]
  (.setText (find-name-text main-frame) name)
  main-frame)

(defn find-name [main-frame]
  (when-let [name-text (find-name-text main-frame)]
    (.getText name-text)))

(defn set-email [main-frame email]
  (.setText (find-email-text main-frame) email)
  main-frame)

(defn find-email [main-frame]
  (when-let [email-text (find-email-text main-frame)]
    (.getText email-text)))

(defn set-phone-number [main-frame phone-number]
  (.setText (find-phone-number-text main-frame) phone-number)
  main-frame)

(defn find-phone-number [main-frame]
  (when-let [phone-number-text (find-phone-number-text main-frame)]
    (.getText phone-number-text)))

(defn set-address [main-frame address]
  (.setText (find-address-text main-frame) address)
  main-frame)

(defn find-address [main-frame]
  (when-let [address-text (find-address-text main-frame)]
    (.getText address-text)))

(defn find-country [main-frame]
  (when-let [country-text (find-country-text main-frame)]
    (.getText country-text)))

(defn set-country [main-frame country]
  (.setText (find-country-text main-frame) country)
  main-frame)

(defn find-province [main-frame]
  (when-let [province-text (find-province-text main-frame)]
    (.getText province-text)))

(defn set-province [main-frame province]
  (.setText (find-province-text main-frame) province)
  main-frame)

(defn find-city [main-frame]
  (when-let [city-text (find-city-text main-frame)]
    (.getText city-text)))

(defn set-city [main-frame city]
  (.setText (find-city-text main-frame) city)
  main-frame)

(defn find-postal-code [main-frame]
  (when-let [postal-code-text (find-postal-code-text main-frame)]
    (.getText postal-code-text)))

(defn set-postal-code [main-frame postal-code]
  (.setText (find-postal-code-text main-frame) postal-code)
  main-frame)

(defn load-name [main-frame]
  (set-name main-frame (:name (name-model/first-current-identity-name))))

(defn load-email [main-frame]
  (set-email main-frame (:email_address (email-model/first-current-identity-email-address))))

(defn load-phone-number [main-frame]
  (set-phone-number main-frame (:phone_number (phone-number-model/first-current-identity-phone-number))))

(defn load-address [main-frame]
  (let [current-address (address-model/first-current-identity-address)]
    (set-address main-frame (:address current-address))
    (set-country main-frame (:country current-address))
    (set-province main-frame (:province current-address))
    (set-city main-frame (:city current-address))
    (set-postal-code main-frame (:postal-code current-address))))

(defn load-data [main-frame]
  (load-address (load-phone-number (load-email (load-name main-frame)))))

(defn save-name [main-frame]
  (name-model/save-or-update-current-identity-name (find-name main-frame))
  main-frame)

(defn save-email [main-frame]
  (email-model/save-or-update-current-identity-email-address (find-email main-frame))
  main-frame)

(defn save-phone-number [main-frame]
  (phone-number-model/save-or-update-current-identity-phone-number (find-phone-number main-frame))
  main-frame)

(defn save-address [main-frame]
  (address-model/save-or-update-current-identity-address (find-address main-frame) (find-country main-frame)
                                                         (find-province main-frame) (find-city main-frame)
                                                         (find-postal-code main-frame))
  main-frame)

(defn update-button-action [event]
  (save-address (save-phone-number (save-email (save-name (seesaw-core/to-frame event))))))

(defn attach-update-action [main-frame]
  (action-utils/attach-listener main-frame "#update-button" update-button-action))

(defn attach [main-frame]
  (attach-update-action main-frame))

(defn init [main-frame]
  (attach (load-data main-frame)))