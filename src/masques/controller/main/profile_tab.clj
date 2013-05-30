(ns masques.controller.main.profile-tab
  (:require [masques.controller.actions.utils :as action-utils]
            ;[masques.model.address :as address-model]
            ;[masques.model.email-address :as email-model]
            ;[masques.model.name :as name-model]
            ;[masques.model.phone-number :as phone-number-model]
            [masques.view.main.profile-tab :as profile-tab-view]
            [seesaw.core :as seesaw-core]))

(defn load-data [main-frame]
  (profile-tab-view/set-data main-frame
                             nil ;(name-model/first-current-identity-name) 
                             nil ;(email-model/first-current-identity-email-address)
                             nil ;(phone-number-model/first-current-identity-phone-number)
                             nil ;(address-model/first-current-identity-address)
                             ))

(defn save-name [data-map]
  ;(name-model/save-or-update-current-identity-name (:name data-map))
  data-map)

(defn save-email [data-map]
  ;(email-model/save-or-update-current-identity-email-address (:email data-map))
  data-map)

(defn save-phone-number [data-map]
  ;(phone-number-model/save-or-update-current-identity-phone-number (:phone-number data-map))
  data-map)

(defn save-address [data-map]
  ;(address-model/save-or-update-current-identity-address (:address data-map))
  data-map)

(defn update-button-action [event]
  (save-address (save-phone-number (save-email (save-name (profile-tab-view/scrape-data
                                                            (seesaw-core/to-frame event)))))))

(defn attach-update-action [main-frame]
  (action-utils/attach-listener main-frame "#update-button" update-button-action))

(defn attach [main-frame]
  (attach-update-action main-frame))

(defn init [main-frame]
  (attach (load-data main-frame)))