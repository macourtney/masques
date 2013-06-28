(ns masques.view.login.create-user
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
    :items [ (term/user-name) (seesaw-core/text :id :user-name-text :preferred-size field-size :maximum-size field-size)]))

(defn password-field [& args]
  (let [password-field (new JPasswordField)]
    (when (and args (> (count args) 0))
      (apply seesaw-core/config! password-field args))
    password-field))

(defn create-password-panel1 []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/enter-password) (password-field :id :password-field1 :preferred-size field-size :maximum-size field-size)]))

(defn create-password-panel2 []
  (seesaw-core/flow-panel
    :align :right
    :hgap 3
    :items [(term/reenter-password) (password-field :id :password-field2 :preferred-size field-size :maximum-size field-size)]))

(defn create-button-panel []
  (seesaw-core/border-panel :east
    (seesaw-core/horizontal-panel :items
      [(seesaw-core/button :id :register-button :text (term/register))
       [:fill-h 3]
       (seesaw-core/button :id :cancel-button :text (term/cancel))])))

(defn create-content []
  (dialog/create-content
    (term/create-user)
    (seesaw-core/vertical-panel
      :border 5
      :items [(create-user-name-panel) [:fill-v 3] (create-password-panel1)[:fill-v 3] (create-password-panel2)
              [:fill-v 5] (create-button-panel)])
    [500 :by 300]))

(defn create [login-frame]
  (view-utils/center-window-on login-frame
    (seesaw-core/frame
      :title (term/masques-create-user)
      :content (create-content)
      :visible? false)))