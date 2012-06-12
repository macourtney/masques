(ns masques.controller.main.peer-tab
  (:require [clj-i2p.core :as clj-i2p]
            [clj-i2p.peer-service.peer :as peer-service]
            [clj-internationalization.core :as clj-i18n]
            [masques.controller.actions.utils :as action-utils]
            [masques.controller.add-destination.add-destination :as add-destination]
            [masques.controller.utils :as controller-utils]
            [masques.model.peer :as peers-model]
            [masques.view.main.peer-tab :as peer-tab-view]
            [seesaw.core :as seesaw-core]))

(def peer-update-listener-key "peer-update-listener")
(def peer-delete-listener-key "peer-delete-listener")

(defn find-peer-tab-panel [main-frame]
  (seesaw-core/select main-frame ["#peer-tab-panel"]))

(defn find-destination-text [main-frame]
  (seesaw-core/select main-frame ["#destination-text"]))

(defn set-destination-text [main-frame destination]
  (.setText (find-destination-text main-frame) (.toBase64 destination)))

(defn create-destination-listener [main-frame]
  (fn [destination]
    (set-destination-text main-frame destination)))

(defn load-destination [main-frame]
  (let [destination (clj-i2p/current-destination)]
    (if destination
      (set-destination-text main-frame destination)
      (clj-i2p/add-destination-listener (create-destination-listener main-frame))))
  main-frame)

(defn find-peer-table [main-frame]
  (seesaw-core/select main-frame ["#peer-table"]))

(defn convert-to-table-peer [peer]
  (when peer
    (assoc peer :notified (when (peer-service/notified? peer) (clj-i18n/yes)))))

(defn reload-table-data [main-frame]
  (when-let [peers (map convert-to-table-peer (peer-service/all-peers))]
    (seesaw-core/config! (find-peer-table main-frame)
      :model [:columns peer-tab-view/peer-table-columns
              :rows peers])))

(defn update-peer-id-table [main-frame peer]
  (controller-utils/update-record-in-table (find-peer-table main-frame)
    (convert-to-table-peer (peers-model/get-record (:id peer)))))

(defn delete-peer-from-table [main-frame peer]
  (controller-utils/delete-record-from-table (find-peer-table main-frame) (:id peer)))

(defn load-peer-table [main-frame]
  (reload-table-data main-frame)
  main-frame)

(defn find-add-button [main-frame]
  (seesaw-core/select main-frame ["#add-button"]))

(defn attach-listener-to-add-button [main-frame]
  (action-utils/attach-listener main-frame "#add-button" 
    (fn [e] (add-destination/show main-frame #(reload-table-data main-frame)))))

(defn save-listener [main-frame listener-key listener]
  (controller-utils/save-component-property (find-peer-tab-panel main-frame) listener-key listener)
  main-frame)

(defn retrieve-listener [main-frame listener-key]
  (controller-utils/retrieve-component-property (find-peer-tab-panel main-frame) listener-key))

(defn remove-listener [main-frame listener-key]
  (let [peer-listener (retrieve-listener main-frame listener-key)]
    (controller-utils/save-component-property (find-peer-tab-panel main-frame) listener-key nil)
    peer-listener))

(defn attach-peer-update-listener [main-frame]
  (let [peer-update-listener (fn [peer] (seesaw-core/invoke-later (update-peer-id-table main-frame peer)))]
    (peers-model/add-peer-update-listener peer-update-listener)
    (save-listener main-frame peer-update-listener-key peer-update-listener)))

(defn attach-peer-delete-listener [main-frame]
  (let [peer-delete-listener (fn [peer] (seesaw-core/invoke-later (delete-peer-from-table main-frame peer)))]
    (peers-model/add-peer-delete-listener peer-delete-listener)
    (save-listener main-frame peer-delete-listener-key peer-delete-listener)))

(defn detach-peer-update-listener [main-frame]
  (peers-model/remove-peer-update-listener (remove-listener main-frame peer-update-listener-key))
  main-frame)

(defn detach-peer-delete-listener [main-frame]
  (peers-model/remove-peer-delete-listener (remove-listener main-frame peer-delete-listener-key))
  main-frame)

(defn attach-peer-listener [main-frame]
  (seesaw-core/listen main-frame
                      :window-opened (fn [e] (attach-peer-delete-listener (attach-peer-update-listener main-frame)))
                      :window-closed (fn [e] (detach-peer-delete-listener (detach-peer-update-listener main-frame))))
  main-frame)

(defn load-data [main-frame]
  (load-peer-table (load-destination main-frame)))

(defn attach [main-frame]
  (attach-peer-listener (attach-listener-to-add-button main-frame)))

(defn init [main-frame]
  (attach (load-data main-frame)))
