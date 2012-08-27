(ns masques.controller.identity.view
  (:require [clj-internationalization.term :as term]
            [clojure.tools.logging :as logging]
            [masques.controller.actions.utils :as actions-utils]
            [masques.controller.utils :as controller-utils]
            [masques.controller.widgets.utils :as widgets-utils]
            [masques.model.identity :as identity-model]
            [masques.view.identity.view :as identity-view]
            [seesaw.core :as seesaw-core]))

(def identity-propery-name "darkexchange.identity")

(defn property-widget [parent-component]
  (.getRootPane (seesaw-core/to-frame parent-component)))

(defn set-identity [parent-component identity]
  (.putClientProperty (property-widget parent-component) identity-propery-name identity))

(defn get-identity [parent-component]
  (.getClientProperty (property-widget parent-component) identity-propery-name))

(defn find-name-label [parent-component]
  (controller-utils/find-component parent-component "#name-label"))

(defn find-public-key-label [parent-component]
  (controller-utils/find-component parent-component "#public-key-label"))

(defn find-algorithm-label [parent-component]
  (controller-utils/find-component parent-component "#public-key-algorithm-label"))

(defn find-is-online-label [parent-component]
  (controller-utils/find-component parent-component "#is-online-label"))

(defn find-status-label [parent-component]
  (controller-utils/find-component parent-component "#offer-table-status-label"))

(defn find-my-trust-score-label [parent-component]
  (controller-utils/find-component parent-component "#my-trust-score-label"))

(defn find-network-trust-score-label [parent-component]
  (controller-utils/find-component parent-component "#network-trust-score-label"))

(defn find-view-offer-button [parent-component]
  (seesaw-core/select parent-component ["#view-offer-button"]))

(defn find-trust-score-slider [parent-component]
  (controller-utils/find-component parent-component "#trust-score-slider"))

(defn attach-cancel-action [parent-component]
  (actions-utils/attach-window-close-listener parent-component "#cancel-button"))

(defn view-offer-if-enabled [parent-component]
  (widgets-utils/do-click-if-enabled (find-view-offer-button parent-component)))

(defn attach-view-offer-actions [parent-component]
  parent-component)

(defn attach [parent-component identity]
  (attach-cancel-action parent-component))

(defn load-name [parent-component identity]
  (seesaw-core/config! (find-name-label parent-component) :text (:name identity)))

(defn load-public-key [parent-component identity]
  (seesaw-core/config! (find-public-key-label parent-component)
    :text (identity-model/shortened-public-key identity)))

(defn load-algorithm [parent-component identity]
  (seesaw-core/config! (find-algorithm-label parent-component)
    :text (:public_key_algorithm identity)))

(defn load-is-online [parent-component identity]
  (seesaw-core/config! (find-is-online-label parent-component)
    :text (if (identity-model/is-online? identity) (term/yes) (term/no))))

(defn set-table-status [parent-component status-text]
  (seesaw-core/config! (find-status-label parent-component) :text (term/status-parens status-text)))

(defn set-complete-table-status [parent-component]
  (seesaw-core/config! (find-status-label parent-component) :text ""))

(defn load-data [parent-component identity]
  (set-identity parent-component identity)
  (load-name parent-component identity)
  (load-public-key parent-component identity)
  (load-algorithm parent-component identity)
  (load-is-online parent-component identity)
  parent-component)

(defn create-and-initialize [main-frame identity]
  (attach (load-data (identity-view/create main-frame) identity) identity))

(defn show [main-frame identity]
  (controller-utils/show (create-and-initialize main-frame identity)))