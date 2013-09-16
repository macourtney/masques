(ns masques.view.friend.panel
  (:require [clj-internationalization.term :as term]
            [masques.view.utils :as view-utils]
            [seesaw.color :as seesaw-color]
            [seesaw.core :as seesaw-core]))

(defn create []
  (seesaw-core/flow-panel
    :id "friend-panel"

    :items ["Friend Panel"]))