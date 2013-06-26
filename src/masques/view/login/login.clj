(ns masques.view.login.login
  (:require [clj-internationalization.term :as term]
            [masques.view.subviews.dialog :as dialog]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JPasswordField]))

(def field-size [300 :by 25])
  
(defn create-user-name-panel []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/user-name)
            (seesaw-core/combobox :id :user-name-combobox :preferred-size field-size :maximum-size field-size)]))

(defn password-field [& args]
  (let [password-field (new JPasswordField)]
    (when (and args (> (count args) 0))
      (apply seesaw-core/config! password-field args))
    password-field))

(defn create-password-panel []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/password)
            (password-field :id :password-field :preferred-size field-size :maximum-size field-size)]))

(defn create-field-panel []
  (seesaw-core/vertical-panel
      :items [(create-user-name-panel) [:fill-v 3] (create-password-panel)]))
      
(defn create-button-panel []
  (seesaw-core/border-panel :east
    (seesaw-core/border-panel
      :north (seesaw-core/horizontal-panel
                :items
                  [(seesaw-core/button :id :cancel-button :text (term/cancel))
                  [:fill-h 3]
                  (seesaw-core/button :id :login-button :text (term/login))])
      :south (seesaw-core/button :id :new-user-button :text (term/create-new-account)))))

(defn create-content []
  (dialog/create-content
    (term/login)
    (seesaw-core/border-panel
      :border 10
      :north (create-field-panel)
      :center (create-button-panel))
    [500 :by 300]))

(defn create []
  (view-utils/center-window
    (seesaw-core/frame
      :title (term/masques-login)
      :content (create-content)
      :visible? false)))