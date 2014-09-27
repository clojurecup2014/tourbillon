(ns tourbillon.event.store
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]))

;; TODO make this polymorphic so that different stores implement
;; a protocol with store-event and get-events
(defrecord LocalEventStore [map-atom]
  component/Lifecycle
  (start [component]
    (log/info "Starting local event store")
    (assoc component :map-atom map-atom :type ::local))

  (stop [component]
    (log/info "Stopping local event store")
    (reset! map-atom {})
    (dissoc component :map-atom :type)))

(defn store-event [store event]
  (let [timestamp (:start event)
        store (:map-atom store)]
    (swap! store update-in [timestamp] conj event)))

(defn get-events [store timestamp]
  (let [store (:map-atom store)]
    (get @store timestamp (list))))

(defn new-store [map-atom]
  (map->LocalEventStore {:map-atom map-atom}))
