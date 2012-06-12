(ns masques.view.main.identity-tab
  (:require [clj-internationalization.core :as clj-i18n]
            [seesaw.core :as seesaw-core]))

(def tab-name (clj-i18n/identity))

(def identity-table-columns [ { :key :name :text (clj-i18n/name) }
                              { :key :public_key :text (clj-i18n/public-key) }
                              { :key :public_key_algorithm :text (clj-i18n/algorithm) }
                              { :key :destination :text (clj-i18n/destination) }
                              { :key :is_online :text (clj-i18n/is-online) }])

(defn create-show-only-online-identities-checkbox []
  (seesaw-core/checkbox :id :show-only-online-identites-checkbox :text (clj-i18n/show-only-online-identities)
    :selected? true))

(defn create-identities-table-title []
  (seesaw-core/horizontal-panel
    :items [ (clj-i18n/identities)
             [:fill-h 3]
             (create-show-only-online-identities-checkbox)]))

(defn create-identity-table-buttons []
  (seesaw-core/horizontal-panel :items 
    [ (seesaw-core/button :id :view-identity-button :text (clj-i18n/view) :enabled? false) ]))

(defn create-title-panel []
  (seesaw-core/border-panel
    :west (create-identities-table-title)
    :east (create-identity-table-buttons)))

(defn create-identity-table []
  (seesaw-core/scrollable
    (seesaw-core/table :id :identity-table :preferred-size [600 :by 300]
      :model [ :columns identity-table-columns ])))

(defn create-identity-table-panel []
  (seesaw-core/border-panel
    :border 5
    :vgap 5
    :north (create-title-panel)
    :center (create-identity-table)))

(defn create-my-identity-panel []
  (seesaw-core/horizontal-panel
    :items [ (seesaw-core/border-panel :size [67 :by 15] :east (seesaw-core/label :text (clj-i18n/my-identity)))
             [:fill-h 3]
             (seesaw-core/border-panel :size [300 :by 20]
               :west (seesaw-core/text :id :my-identity :text "data" :editable? false))]))

(defn create []
  (seesaw-core/border-panel
    :id :identity-tab-panel
    :border 5
    :vgap 5
    :north (create-my-identity-panel)
    :center (create-identity-table-panel)))