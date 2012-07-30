(ns masques.view.subviews.profile-data
  (:require [clj-internationalization.core :as clj-i18n]
            [seesaw.core :as seesaw-core]))

(defn create-label-value-pair-panel [text text-key]
  (seesaw-core/border-panel
    :west (seesaw-core/horizontal-panel
            :size [350 :by 15]
            :items [(seesaw-core/label :text text)
                    [:fill-h 3]
                    (seesaw-core/text :id text-key :text "data" :preferred-size [200 :by 20])])))

(defn create-name-panel []
  (create-label-value-pair-panel (clj-i18n/name) :name-text))

(defn create-email-panel []
  (create-label-value-pair-panel (clj-i18n/email) :email-text))

(defn create-phone-number-panel []
  (create-label-value-pair-panel (clj-i18n/phone-number) :phone-number-text))

(defn create-address-text-area []
  (doto (seesaw-core/text
          :id :address-text
          :multi-line? true
          :preferred-size [400 :by 50])
    (.setLineWrap true)))

(defn create-address-text []
  (seesaw-core/scrollable (create-address-text-area)))

(defn create-address-panel []
  (seesaw-core/border-panel
    :north (clj-i18n/address)
    :west (create-address-text)))

(defn create-country-state-city-zip-panel []
  (seesaw-core/border-panel
    :west (seesaw-core/horizontal-panel
            :items [(seesaw-core/label :text (clj-i18n/country))
                    [:fill-h 3]
                    (seesaw-core/text :id :country-text :text "data" :preferred-size [100 :by 20])
                    [:fill-h 3]
                    (seesaw-core/label :text (clj-i18n/province-or-state))
                    [:fill-h 3]
                    (seesaw-core/text :id :province-text :text "data" :preferred-size [100 :by 20])
                    [:fill-h 3]
                    (seesaw-core/label :text (clj-i18n/city))
                    [:fill-h 3]
                    (seesaw-core/text :id :city-text :text "data" :preferred-size [100 :by 20])
                    [:fill-h 3]
                    (seesaw-core/label :text (clj-i18n/postal-code))
                    [:fill-h 3]
                    (seesaw-core/text :id :postal-code-text :text "data" :preferred-size [100 :by 20])])))

(defn create-profile-panel []
  (seesaw-core/vertical-panel
      :items [(create-name-panel)
              [:fill-v 3]
              (create-email-panel)
              [:fill-v 3]
              (create-phone-number-panel)
              [:fill-v 3]
              (create-address-panel)
              [:fill-v 3]
              (create-country-state-city-zip-panel)]))

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

(defn read-record [record key]
  (if (map? record)
    (get record key)
    record))

(defn text
  ([text-field]
    (when text-field (.getText text-field)))
  ([text-field record key]
    (.setText text-field (read-record record key))))

(defn set-name [main-frame name]
  (text (find-name-text main-frame) name :name)
  main-frame)

(defn find-name [main-frame]
  (text (find-name-text main-frame)))

(defn set-email [main-frame email]
  (text (find-email-text main-frame) email :email_address)
  main-frame)

(defn find-email [main-frame]
  (text (find-email-text main-frame)))

(defn set-phone-number [main-frame phone-number]
  (text (find-phone-number-text main-frame) phone-number :phone_number)
  main-frame)

(defn find-phone-number [main-frame]
  (text (find-phone-number-text main-frame)))

(defn set-address [main-frame address]
  (text (find-address-text main-frame) address :address)
  main-frame)

(defn find-address [main-frame]
  (text (find-address-text main-frame)))

(defn set-country [main-frame country]
  (text (find-country-text main-frame) country :country)
  main-frame)

(defn find-country [main-frame]
  (text (find-country-text main-frame)))

(defn set-province [main-frame province]
  (text (find-province-text main-frame) province :province)
  main-frame)

(defn find-province [main-frame]
  (text (find-province-text main-frame)))

(defn set-city [main-frame city]
  (text (find-city-text main-frame) city :city)
  main-frame)

(defn find-city [main-frame]
  (text (find-city-text main-frame)))

(defn set-postal-code [main-frame postal-code]
  (text (find-postal-code-text main-frame) postal-code :postal-code)
  main-frame)

(defn find-postal-code [main-frame]
  (text (find-postal-code-text main-frame)))

(defn set-full-address [main-frame address]
  (set-address main-frame address)
  (set-country main-frame address)
  (set-province main-frame address)
  (set-city main-frame address)
  (set-postal-code main-frame address))

(defn set-data
  ([main-frame profile]
    (set-data main-frame (:name profile) (:email profile) (:phone-number profile) (:address profile)))
  ([main-frame name email phone-number address]
    (set-full-address (set-phone-number (set-email (set-name main-frame name) email) phone-number) address)))

(defn scrape-data [main-frame]
  { :name (find-name main-frame)
    :email (find-email main-frame)
    :phone-number (find-phone-number main-frame)
    :address { :address (find-address main-frame)
               :country (find-country main-frame)
               :province (find-province main-frame)
               :city (find-city main-frame)
               :postal-code (find-postal-code main-frame)} })