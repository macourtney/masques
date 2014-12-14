(ns masques.model.peer-persister
  (:require [clj-i2p.core :as clj-i2p]
            [clj-i2p.peer-service.persister-protocol :as persister-protocol]
            [clojure.tools.logging :as logging]
            [masques.model.friend-request :as friend-request]
            [masques.model.profile :as profile])
  (:use masques.model.base))

(def online-peers (atom []))

(defn find-online-peer
  "Finds the the online peer with the given peer id."
  [peer-id]
  (let [peer-id (id peer-id)]
    (some #(when (= peer-id (id %1)) %1) @online-peers)))

(defn set-offline
  "Sets the peer with the given id as offline."
  [peer-id]
;  (logging/debug "Setting offline:" peer-id)
  (let [peer-id (id peer-id)]
    (swap! online-peers (fn [peers] (filter #(not (= peer-id (id %))) peers)))))

(defn set-online
  "Sets the peer with the given id as online."
  [peer-id]
;  (logging/debug "Setting online:" peer-id)
  (let [peer-id2 (id peer-id)
        online-peer { clojure-id peer-id2
                      profile/alias-key (profile/alias peer-id) }]
    (swap! online-peers conj online-peer)))

(defn update-profile-with-id
  "Adds the id from the given peer to the given profile."
  [profile peer]
  (if-let [peer-id (id peer)]
    (assoc profile clojure-id peer-id)
    profile))

(defn update-profile-with-destination
  "Adds the destination from the given peer to the given profile."
  [profile peer]
  (if-let [peer-destination (:destination peer)]
    (assoc profile profile/destination-key peer-destination)
    profile))

(defn convert-peer-to-profile
  "Converts the given peer to a profile map."
  [peer]
  (update-profile-with-destination
    (update-profile-with-id {} peer)
    peer))

(deftype DbPeerPersister [peer-update-listeners peer-delete-listeners]
  persister-protocol/PeerPersister
  (insert-peer [persister peer])

  (update-peer [persister peer]
    (when (:notified peer)
      (set-online peer)))

  (delete-peer [persister peer])

  (all-peers [persister])

  (all-foreign-peers [persister])

  (find-peer [persister peer]
    (when-let [peer-profile (profile/find-profile
                              (convert-peer-to-profile peer))]
      (when (friend-request/profile-approved? peer-profile)
        peer-profile)))

  (find-all-peers [persister peer])

  (last-updated-peer [persister])

  (all-unnotified-peers [persister])

  (all-notified-peers [persister])

  (add-peer-update-listener [persister listener]
    (swap! peer-update-listeners conj listener))

  (remove-peer-update-listener [persister listener]
    (swap! peer-update-listeners
           (fn [listeners] (remove #(not (= % listener)) listeners))))

  (add-peer-delete-listener [persister listener]
    (swap! peer-delete-listeners conj listener))

  (remove-peer-delete-listener [persister listener]
    (swap! peer-delete-listeners
           (fn [listeners] (remove #(not (= % listener)) listeners))))

  (default-destinations [persister]
    (profile/all-destinations))

  (peers-downloaded? [persister]
    true)

  (set-peers-downloaded? [persister value]))

(defn create-peer-persister
  "Creates a new instance of DbPeerPersister and returns it."
  []
  (DbPeerPersister. (atom []) (atom [])))

(defn send-message-fail-listener
  "Removes the given destination from the online peers."
  [destination _]
  (when-let [offline-profile (profile/find-profile
                               { profile/destination-key
                                 (clj-i2p/as-destination-str destination) })]
    (set-offline offline-profile)))

(defn init
  "Also, creates a new instance of DbPeerPersister and registers it with the
persister protocol if one is not already registered."
  []
  (when (not (persister-protocol/protocol-registered?))
    (persister-protocol/register (create-peer-persister)))
  (clj-i2p/add-send-message-fail-listener send-message-fail-listener))