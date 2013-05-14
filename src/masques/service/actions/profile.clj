(ns masques.service.actions.profile
  (:refer-clojure :exclude [name])
  (:require ;[masques.model.address :as address-model]
            ;[masques.model.email-address :as email-model]
            [masques.model.friend :as friend-model] 
            ;[masques.model.name :as name-model]
            ;[masques.model.phone-number :as phone-number-model]
            ;[masques.model.permission :as permission-model]
            [masques.service.request-map-utils :as request-map-utils]))

(def action "profile")

(defn name [sender-friend]
;  (when (friend-model/has-read-permission? sender-friend permission-model/profile-name-permission)
;    (:name (name-model/first-current-identity-name)))
  )

(defn email [sender-friend]
;  (when (friend-model/has-read-permission? sender-friend permission-model/profile-email-permission)
;    (:email_address (email-model/first-current-identity-email-address)))
  )

(defn phone-number [sender-friend]
;  (when (friend-model/has-read-permission? sender-friend permission-model/profile-phone-number-permission)
;    (:phone_number (phone-number-model/first-current-identity-phone-number)))
  )

(defn address [sender-friend current-address]
;  (when (friend-model/has-read-permission? sender-friend permission-model/profile-address-permission)
;    (:address current-address))
  )

(defn country [sender-friend current-address]
;  (when (friend-model/has-read-permission? sender-friend permission-model/profile-country-permission)
;    (:country current-address))
  )

(defn province [sender-friend current-address]
;  (when (friend-model/has-read-permission? sender-friend permission-model/profile-province-permission)
;    (:province current-address))
  )

(defn city [sender-friend current-address]
;  (when (friend-model/has-read-permission? sender-friend permission-model/profile-city-permission)
;    (:city current-address))
  )

(defn postal-code [sender-friend current-address]
;  (when (friend-model/has-read-permission? sender-friend permission-model/profile-postal-code-permission)
;    (:postal_code current-address))
  )

(defn full-address [sender-friend]
;  (let [current-address (address-model/first-current-identity-address)
;        full-address-map { :address (address sender-friend current-address)
;                           :country (country sender-friend current-address)
;                           :province (province sender-friend current-address)
;                           :city (city sender-friend current-address)
;                           :postal-code (postal-code sender-friend current-address) }]
;    (when (some identity (vals full-address-map))
;      full-address-map))
  )

(defn run [request-map]
  (let [sender-friend (request-map-utils/sender-friend? request-map)]
    { :data
      { :name (name sender-friend)
        :email (email sender-friend)
        :phone-number (phone-number sender-friend)
        :address (full-address sender-friend) }}))