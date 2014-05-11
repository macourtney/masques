(ns masques.view.group.all-groups-combobox-model
  (:require [masques.model.grouping :as grouping-model]
            [masques.view.utils.korma-combobox-model :as korma-combobox-model]
            [masques.view.utils :as utils]))

(deftype AllGroupsComboboxModel []
  korma-combobox-model/DbComboBoxModel
  (record-count [this]
    (grouping-model/count-groups))
  
  (record-at [this index]
    (grouping-model/find-combobox-group index)))

(defn create
  "Creates a new combobox model for use in the groups combobox."
  []
  (korma-combobox-model/create (new AllGroupsComboboxModel)))