(ns masques.controller.main.main-frame
  (:require [clojure.tools.logging :as logging]
            [masques.controller.utils :as controller-utils]
            [masques.view.main.main-frame :as view-main-frame]))

(defn show []
  (controller-utils/show
    (view-main-frame/create)))