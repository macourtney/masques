(ns masques.view.main.home-tab
  (:require [clj-internationalization.core :as clj-i18n]
            ;[darkexchange.view.main.home.open-offer-panel :as open-offer-panel]
            ;[darkexchange.view.main.home.open-trade-panel :as open-trade-panel]
            [seesaw.core :as seesaw-core]))

(def tab-name (clj-i18n/home))

(defn create []
  (seesaw-core/vertical-panel
    :items [
            ;(open-trade-panel/create)
            [:fill-v 3]
            ;(open-offer-panel/create)
            ]))