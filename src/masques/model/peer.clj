(ns masques.model.peer
  (:require [clj-i2p.peer-service.persister-protocol :as persister-protocol]
            [clj-record.boot :as clj-record-boot]
            [clojure.data.xml :as data-xml]
            [clojure.java.io :as java-io]
            [clojure.tools.loading-utils :as loading-utils]
            [config.environment :as environment]
            [masques.model.property :as property])
  (:use masques.model.base)
  (:import [java.sql Clob]
           [java.text SimpleDateFormat]
           [java.util Date]))

(def peers-file-name "peers.txt")
(def development-peers-file-name "development-peers.txt")

(def peer-update-listeners (atom []))

(def peer-delete-listeners (atom []))

(defn add-peer-update-listener [listener]
  (swap! peer-update-listeners conj listener))

(defn remove-peer-update-listener [listener]
  (swap! peer-update-listeners remove-listener listener))

(defn peer-update [peer]
  (doseq [listener @peer-update-listeners]
    (listener peer)))

(defn peer-update-listener-count []
  (count @peer-update-listeners))

(defn add-peer-delete-listener [listener]
  (swap! peer-delete-listeners conj listener))

(defn remove-peer-delete-listener [listener]
  (swap! peer-delete-listeners remove-listener listener))

(defn peer-delete [peer]
  (doseq [listener @peer-delete-listeners]
    (listener peer)))

(defn peer-delete-listener-count []
  (count @peer-delete-listeners))

(defn peer-clean-up [peer]
  (clean-clob-key peer :destination))

(clj-record.core/init-model
  (:callbacks (:after-update peer-update)
              (:after-insert peer-update)
              (:after-load peer-clean-up)
              (:after-destroy peer-delete)))

(defn peers-text-resource []
  (if (= (environment/environment-name) "development")
    development-peers-file-name
    peers-file-name))

(defn peers-text-reader []
  (java-io/reader (java-io/resource (peers-text-resource))))

(deftype DbPeerPersister []
  persister-protocol/PeerPersister
  (insert-peer [persister peer]
    ;(insert peer)
    )

  (update-peer [persister peer]
    ;(update peer)
    )

  (delete-peer [persister peer]
    ;(destroy-record peer)
    )

  (all-peers [persister]
    ;(find-records [true])
    )

  (all-foreign-peers [persister]
    ;(find-by-sql ["SELECT * FROM peers WHERE local IS NULL OR local = 0"])
    )

  (find-peer [persister peer]
    ;(find-record peer)
    )

  (find-all-peers [persister peer]
    ;(find-records peer)
    )

  (last-updated-peer [persister]
    ;(first (find-by-sql ["SELECT * FROM peers WHERE local IS NULL OR local = 0 ORDER BY updated_at DESC LIMIT 1"]))
    )

  (all-unnotified-peers [persister]
    ;(find-by-sql ["SELECT * FROM peers WHERE notified IS NULL"])
    )

  (all-notified-peers [persister]
    ;(find-by-sql ["SELECT * FROM peers WHERE notified IS NOT NULL AND NOT notified = 0"])
    )

  (add-peer-update-listener [persister listener]
    (add-peer-update-listener listener))

  (remove-peer-update-listener [persister listener]
    (remove-peer-update-listener listener))

  (add-peer-delete-listener [persister listener]
    (add-peer-delete-listener listener))

  (remove-peer-delete-listener [persister listener]
    (remove-peer-delete-listener listener))

  (default-destinations [persister]
    (filter #(< 0 (count (.trim %1))) (line-seq (peers-text-reader))))

  (peers-downloaded? [persister]
    (property/peers-downloaded?))

  (set-peers-downloaded? [persister value]
    (property/set-peers-downloaded? value)))

(defn create-peer-persister
  "Creates a new instance of DbPeerPersister and returns it."
  []
  (DbPeerPersister.))

(defn init
  "Creates a new instance of DbPeerPersister and registers it with the persister protocol if one is not already
registered."
  []
  (when (not (persister-protocol/protocol-registered?))
    (persister-protocol/register (create-peer-persister))))

(defn local? [peer]
  (as-boolean (:local peer)))

(defn xml [peer]
  (data-xml/element :peer (select-keys peer [:destination])))