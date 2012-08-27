(ns masques.view.main.home-tab
  (:require [clj-internationalization.term :as term]
            ;[darkexchange.view.main.home.open-offer-panel :as open-offer-panel]
            ;[darkexchange.view.main.home.open-trade-panel :as open-trade-panel]
            [seesaw.core :as seesaw-core]))

(def tab-name (term/home))

(defn create []
  (seesaw-core/vertical-panel
    :items [
            ;(open-trade-panel/create)
            [:fill-v 3]
            ;(open-offer-panel/create)
            ]))