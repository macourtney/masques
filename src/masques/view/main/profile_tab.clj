(ns masques.view.main.profile-tab
  (:require [clj-internationalization.core :as clj-i18n]
            [masques.view.subviews.profile-data :as profile-data]
            [seesaw.core :as seesaw-core]))

(def tab-name (clj-i18n/profile))

(defn create-button []
  (seesaw-core/border-panel
    :west (seesaw-core/button :id :update-button :text (clj-i18n/update))))

(defn create []
  (seesaw-core/border-panel
    :border 9
    :north (profile-data/create-profile-panel)
    :south (create-button)))

(defn set-data [main-frame name email phone-number address]
  (profile-data/set-data main-frame name email phone-number address))

(defn scrape-data [main-frame]
  (profile-data/scrape-data main-frame))