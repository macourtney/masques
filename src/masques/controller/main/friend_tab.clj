(ns masques.controller.main.friend-tab
  (:require [masques.controller.actions.utils :as action-utils]
            [masques.controller.utils :as controller-utils]
            [masques.model.friend :as friends-model]
            [masques.view.main.friend-tab :as friend-tab-view]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table]))

(def friend-add-listener-key "friend-add-listener")
(def friend-delete-listener-key "friend-delete-listener")

(defn friend-panel [main-frame]
  (seesaw-core/select main-frame ["#friend-tab-panel"]))

(defn find-friend-table [main-frame]
  (seesaw-core/select main-frame ["#friend-table"]))

(defn find-friend-xml-text [main-frame]
  (seesaw-core/select main-frame ["#friend-text"]))

(defn convert-to-table-friend [friend]
  { :id (:id friend) :name (friends-model/friend-name friend) })

(defn reload-table-data [main-frame]
  (when-let [friends (map convert-to-table-friend (friends-model/all-friends))]
    (seesaw-core/config! (find-friend-table main-frame)
      :model [:columns friend-tab-view/friend-table-columns
              :rows friends])))

(defn friend-count
  "Returns the number of rows in the friend table."
  [main-frame]
  (seesaw-table/row-count (find-friend-table main-frame)))

(defn all-friends
  "Returns all of the friends from the friend table."
  [main-frame]
  (seesaw-table/value-at (find-friend-table main-frame) (range (friend-count main-frame))))

(defn friend-xml-text [main-frame]
  (seesaw-core/text (find-friend-xml-text main-frame)))

(defn load-friend-table [main-frame]
  (reload-table-data main-frame)
  main-frame)

(defn load-friend-xml-text [main-frame]
  (.setText (find-friend-xml-text main-frame)
        (friends-model/friend-xml-string))
  main-frame)

(defn find-add-button [main-frame]
  (seesaw-core/select main-frame ["#add-friend-button"]))

(defn attach-listener-to-add-button [main-frame]
  ;(action-utils/attach-listener main-frame "#add-button" 
  ;  (fn [e] (add-destination/show main-frame #(reload-table-data main-frame))))
  main-frame)

(defn friend-add-listener [main-frame friend]
  )

(defn friend-delete-listener [main-frame friend]
  )

(defn save-listener [main-frame listener-key listener]
  (controller-utils/save-component-property (friend-panel main-frame) listener-key listener)
  listener)

(defn remove-listener [main-frame listener-key]
  (controller-utils/remove-component-property (friend-panel main-frame) listener-key))

(defn attach-friend-add-listener [main-frame]
  (friends-model/add-friend-add-listener
    (save-listener main-frame friend-add-listener-key #(friend-add-listener main-frame %)))
  main-frame)

(defn attach-friend-delete-listener [main-frame]
  (friends-model/add-friend-delete-listener
    (save-listener main-frame friend-delete-listener-key #(friend-delete-listener main-frame %)))
  main-frame)

(defn detach-friend-add-listener [main-frame]
  (friends-model/remove-friend-add-listener (remove-listener main-frame friend-add-listener-key))
  main-frame)

(defn detach-friend-delete-listener [main-frame]
  (friends-model/remove-friend-delete-listener (remove-listener main-frame friend-delete-listener-key))
  main-frame)

(defn attach-friend-listener [main-frame]
  (seesaw-core/listen main-frame
                      :window-opened (fn [e] (attach-friend-add-listener
                                               (attach-friend-delete-listener
                                                 main-frame)))
                      :window-closed (fn [e] (detach-friend-add-listener
                                               (detach-friend-delete-listener
                                                 main-frame))))
  main-frame)

(defn load-data [main-frame]
  (load-friend-table (load-friend-xml-text main-frame)))

(defn attach [main-frame]
  (attach-friend-listener (attach-listener-to-add-button main-frame)))

(defn init [main-frame]
  (attach (load-data main-frame)))
