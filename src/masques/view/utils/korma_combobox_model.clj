(ns masques.view.utils.korma-combobox-model
  (:import [javax.swing ComboBoxModel]))

(defprotocol DbComboBoxModel
  "A model for displaying data to a combobox from a korma db."

  (record-count [this] "Returns the number of records for this model.")
  
  (record-at [this index] "Returns the record at the given index"))

(deftype KormaComboBoxModel [combobox-model-listener-set db-combobox-model
                          selected-element]
  ComboBoxModel

  (getElementAt [this index]
    (record-at db-combobox-model index))
  
  (getSize [this]
    (record-count db-combobox-model))
  
  (setSelectedItem [this record]
    (reset! selected-element record))
  
  (getSelectedItem [this]
    @selected-element)
  
  (addListDataListener [this listener]
    (reset! combobox-model-listener-set
            (conj @combobox-model-listener-set listener)))
  
  (removeListDataListener [this listener]
    (reset! combobox-model-listener-set
            (disj @combobox-model-listener-set listener))))

(defn create
  "Creates a new KormaComboBoxModel using the given DbComboBoxModel"
  [db-combobox-model]
  (KormaComboBoxModel. (atom #{}) db-combobox-model (atom nil)))