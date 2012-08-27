(ns masques.view.login.create-user
  (:require [clj-internationalization.term :as term]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JPasswordField]))

(defn create-user-name-panel []
  (seesaw-core/horizontal-panel :items
    [ (term/user-name)
      [:fill-h 3]
      (seesaw-core/text :id :user-name-text :preferred-size [150 :by 25])]))

(defn password-field [& args]
  (let [password-field (new JPasswordField)]
    (when (and args (> (count args) 0))
      (apply seesaw-core/config! password-field args))
    password-field))

(defn create-password-panel1 []
  (seesaw-core/horizontal-panel :items
    [ (term/enter-password)
      [:fill-h 3]
      (password-field :id :password-field1 :preferred-size [120 :by 25])]))

(defn create-password-panel2 []
  (seesaw-core/horizontal-panel :items
    [ (term/reenter-password)
      [:fill-h 3]
      (password-field :id :password-field2 :preferred-size [120 :by 25])]))

(defn create-button-panel []
  (seesaw-core/border-panel :east
    (seesaw-core/horizontal-panel :items
      [(seesaw-core/button :id :register-button :text (term/register))
       [:fill-h 3]
       (seesaw-core/button :id :cancel-button :text (term/cancel))])))

(defn create-content []
  (seesaw-core/vertical-panel
    :border 5
    :items [(create-user-name-panel) [:fill-v 3] (create-password-panel1)[:fill-v 3] (create-password-panel2)
            [:fill-v 5] (create-button-panel)]))

(defn create [login-frame]
  (view-utils/center-window-on login-frame
    (seesaw-core/frame
      :title (term/masques-create-user)
      :content (create-content)
      :visible? false)))