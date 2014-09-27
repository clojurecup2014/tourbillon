(ns tourbillon.event.core
  (:require [taoensso.timbre :as log]))

(def not-nil? (complement nil?))

(defprotocol Temporal
  "Something that exists at one or more points in time"
  (is-immediate? [this])
  (is-future? [this])
  (is-recurring? [this])
  (next-interval [this]))

(defrecord Event [id job-id start interval data]
  Temporal
  (is-immediate? [this] (every? nil? [start interval]))
  (is-future? [this] (and (not-nil? start)
                          (nil? interval)))
  (is-recurring? [this] (every? not-nil? [start interval]))
  (next-interval [this] (->Event id job-id (+ start interval) interval data)))

(defn create-event
  ([id job-id data] (create-event id job-id nil nil data))
  ([id job-id at data] (create-event id job-id at nil data))
  ([id job-id start interval data] (->Event id job-id start interval data)))
