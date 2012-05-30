(ns masques.controller.main.main-frame
  (:require [clojure.tools.logging :as logging]
            [masques.controller.main.home-tab :as home-tab]
            [masques.controller.main.identity-tab :as identity-tab]
            [masques.controller.main.main-menu-bar :as main-menu-bar]
            [masques.controller.main.peer-tab :as peer-tab]
            [masques.controller.main.profile-tab :as profile-tab]
            [masques.controller.utils :as controller-utils]
            [masques.view.main.main-frame :as view-main-frame]))

(defn show []
  (controller-utils/show
    (profile-tab/init (identity-tab/init (home-tab/init (peer-tab/init (main-menu-bar/init (view-main-frame/create))))))))