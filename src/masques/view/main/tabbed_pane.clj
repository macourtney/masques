(ns masques.view.main.tabbed-pane
  (:require [masques.view.main.friend-tab :as friend-tab]
            [masques.view.main.home-tab :as home-tab]
            [masques.view.main.identity-tab :as identity-tab]
            [masques.view.main.peer-tab :as peer-tab]
            [masques.view.main.profile-tab :as profile-tab]
            [seesaw.core :as seesaw-core]))

(defn create-tabs []
  [ { :title home-tab/tab-name :content (home-tab/create) }
    { :title profile-tab/tab-name :content (profile-tab/create) }
    { :title friend-tab/tab-name :content (friend-tab/create) }
    { :title identity-tab/tab-name :content (identity-tab/create) }
    { :title peer-tab/tab-name :content (peer-tab/create) } ])

(defn create []
  (seesaw-core/tabbed-panel :id :main-tabbed-pane
    :tabs (create-tabs)))