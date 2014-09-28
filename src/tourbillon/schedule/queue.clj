(ns tourbillon.schedule.queue
  (:require [taoensso.timbre :as log]))

;; TODO: replace queue with a structure that actively pushes events
;; to subscribers
(defn enqueue! [queue item]
  (do
    ; (log/info ["enqueue" item])
    (swap! queue conj item)))

(defn dequeue! [queue]
  (when-let [out (peek @queue)]
    ; (log/info ["dequeue" out])
    (swap! queue pop)
    out))

(defn new-queue []
  (atom (clojure.lang.PersistentQueue/EMPTY)))
