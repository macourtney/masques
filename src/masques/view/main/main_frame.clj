(ns masques.view.main.main-frame
  (:require [clj-internationalization.term :as term]
            [masques.view.main.main-menu-bar :as main-menu-bar]
            [masques.view.main.tabbed-pane :as tabbed-pane]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(defn create []
  (view-utils/center-window
    (seesaw-core/frame
      :title (term/masques)
      :menubar (main-menu-bar/create)
      :content (tabbed-pane/create)
      :on-close :exit
      :visible? false)))