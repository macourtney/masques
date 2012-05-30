(ns masques.view.main.profile-tab
  (:require [clj-internationalization.core :as clj-i18n]
            [seesaw.core :as seesaw-core]))

(def tab-name (clj-i18n/profile))

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
          :preferred-size [800 :by 100])
    (.setLineWrap true)))

(defn create-address-text []
  (seesaw-core/scrollable (create-address-text-area)))

(defn create-address-panel []
  (seesaw-core/border-panel
    :north (clj-i18n/address)
    :center (create-address-text)))

(defn create-button []
  (seesaw-core/button :id :update-button :text (clj-i18n/update)))

(defn create []
  (seesaw-core/border-panel
    :border 9
    :north
    (seesaw-core/vertical-panel
      :items [(create-name-panel)
              [:fill-v 3]
              (create-email-panel)
              [:fill-v 3]
              (create-phone-number-panel)
              [:fill-v 3]
              (create-address-panel)
              [:fill-v 3]
              (create-button)])))