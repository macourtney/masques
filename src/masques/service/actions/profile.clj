(ns masques.service.actions.profile
  (:require [masques.model.address :as address-model]
            [masques.model.email-address :as email-model] 
            [masques.model.name :as name-model]
            [masques.model.phone-number :as phone-number-model]
            [masques.service.request-map-utils :as request-map-utils]))

(def action "profile")

(defn run [request-map]
  (when (request-map-utils/sender-friend? request-map)
    (let [current-address (address-model/first-current-identity-address)]
      { :data
        { :name (:name (name-model/first-current-identity-name))
          :email_address (:email_address (email-model/first-current-identity-email-address))
          :phone_number (:phone_number (phone-number-model/first-current-identity-phone-number))
          :address { :address (:address current-address)
                     :country (:country current-address)
                     :province (:province current-address)
                     :city (:city current-address)
                     :postal-code (:postal-code current-address) } }})))