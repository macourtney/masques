(ns masques.view.profile.panel
  (:require [clj-internationalization.term :as term]
            [masques.view.subviews.panel :as panel-subview]
            [masques.view.utils :as view-utils]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing ImageIcon]))

(def profile-name-id :profile-name)
  
(defn create-header []
  (seesaw-core/border-panel
    :west (seesaw-core/flow-panel :items [(seesaw-core/text :id profile-name-id :columns 20)])
    :east (seesaw-core/vertical-panel :items [(panel-subview/create-panel-label (term/your-profile))])))

(defn create-profile-page-tools []
  (seesaw-core/vertical-panel
    :items [(seesaw-core/button :icon (ImageIcon. (ClassLoader/getSystemResource "profile.png")))
            (panel-subview/create-button :change-avatar-button (term/change-avatar))
            [:fill-v 15]
            (term/identity)
            (panel-subview/create-button :identity-text-button (term/text))
            (panel-subview/create-button :identity-qr-code-button (term/qr-code))
            [:fill-v 15]
            (term/time-zone)
            (seesaw-core/label :id :time-zone-label :text "EST (GMT -5)" :font { :name "DIALOG" :style :plain :size 10 })
            (panel-subview/create-button :time-zone-change-button (term/change))
            [:fill-v 15]
            (term/password)
            (panel-subview/create-button :password-change-button (term/change))]))
  
(defn create-profile-page-body []
  (seesaw-core/scrollable
    (seesaw-core/text :id :profile-body-text :multi-line? true :wrap-lines? true)))

(defn create-profile-page []
  (seesaw-core/border-panel
    :west (create-profile-page-tools)
    :center (create-profile-page-body)
    
    :hgap 5
    :border 11))

(defn create-visibility []
  (seesaw-core/flow-panel :items ["visibility"]))

(defn create-shares []
  (seesaw-core/flow-panel :items ["shares"]))

(defn create-advanced []
  (seesaw-core/flow-panel :items ["advanced"]))

(defn create-body []
  (seesaw-core/tabbed-panel
    :tabs [{ :title (term/profile-page) :content (create-profile-page) }
           { :title (term/visibility) :content (create-visibility) }
           { :title (term/shares) :content (create-shares) }
           { :title (term/advanced) :content (create-advanced) }]))

(defn create []
  (seesaw-core/border-panel
    :id "profile-panel"

    :north (create-header)
    :center (create-body)

    :vgap 10
    :border 11))

(defn find-profile-name-text
  "Returns the profile name text field in the given profile panel view."
  [view]
  (view-utils/find-component view (str "#" (name profile-name-id))))
  
(defn set-profile-name-text
  "Sets the text of the profile name text field to the given text"
  [view text]
  (seesaw-core/config! (find-profile-name-text view) :text text))

(defn fill-panel
  "Fills the given profile panel view with data from the given profile."
  [view profile]
  (set-profile-name-text view (:alias profile)))