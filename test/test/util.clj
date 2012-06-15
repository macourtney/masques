(ns test.util
  (:require [masques.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(defn create-test-window [panel]
  (view-utils/center-window
    (seesaw-core/frame
      :title "Test Window"
      :content panel)))

(defn show [panel]
  (seesaw-core/show!
    (create-test-window panel)))

(defn show-and-wait
  ([panel] (show-and-wait panel 5000))
  ([panel wait-time]
    (let [test-frame (show panel)]
    (Thread/sleep wait-time)
    (.hide test-frame)
    (.dispose test-frame))))