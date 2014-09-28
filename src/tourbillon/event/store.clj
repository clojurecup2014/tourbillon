(ns tourbillon.event.store
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]))

(defn ^:dynamic get-time []
  (int (/ (System/currentTimeMillis) 1000)))

;; TODO make this polymorphic so that different stores implement
;; a protocol with store-event! and get-events
(defrecord LocalEventStore [map-atom last-check]
  component/Lifecycle
  (start [component]
    (log/info "Starting local event store")
    (let [defaults {:min (get-time) :last-check (get-time)}]
      (swap! map-atom #(merge defaults %))
      (assoc component :type ::local
                       :map-atom map-atom
                       :last-check (or last-check (get-time)))))

  (stop [component]
    (log/info "Stopping local event store")
    (dissoc component :map-atom :type :last-check)))

(defn store-event! [store event]
  (let [timestamp (:start event)
        map-atom (:map-atom store)
        last-check (:last-check store)]
    (swap! map-atom update-in [timestamp] conj event)))

; TODO: we may need to change the reference type wrapping the store
; to a ref and run this in a transaction
(defn get-events [store timestamp]
  (let [map-atom (:map-atom store)
        last-check (:last-check store)
        all-timestamps (range last-check (inc timestamp))
        events (mapcat #(get @map-atom % (list)) all-timestamps)]
    (swap! map-atom #(apply dissoc % all-timestamps))
    events))

(defn new-store
  ([map-atom] (new-store map-atom (get-time)))
  ([map-atom last-check] (map->LocalEventStore {:map-atom map-atom
                                                :last-check last-check})))
