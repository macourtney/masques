(ns masques.view.main.main-frame
  (:require [clj-internationalization.core :as clj-i18n]
            [masques.view.main.main-menu-bar :as main-menu-bar]
            [masques.view.main.tabbed-pane :as tabbed-pane]
            [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(defn create []
  (view-utils/center-window
    (seesaw-core/frame
      :title (clj-i18n/masques)
      :menubar (main-menu-bar/create)
      :content (tabbed-pane/create)
      :on-close :exit
      :visible? false)))