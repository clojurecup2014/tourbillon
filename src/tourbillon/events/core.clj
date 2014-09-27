(ns tourbillon.events.core
  (:require [taoensso.timbre :as log]))


(defprotocol Temporal
  "Something that exists at one or more points in time"
  (is-recurring? [this])
  (next-interval [this]))

(defrecord Event [id retrieved start interval data]
  Temporal
  (is-recurring? [this] (not (nil? interval)))
  (next-interval [this] (->Event id nil (+ start interval) interval data)))

(defn create-event
  ([at data] (create-event at nil data))
  ([start interval data] (->Event nil nil start interval data)))
