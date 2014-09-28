(ns tourbillon.schedule.queue)

(defn enqueue! [queue item]
  (swap! queue conj item))

(defn dequeue! [queue]
  (when-let [out (peek @queue)]
    (swap! queue pop)
    out))

(defn new-queue []
  (atom (clojure.lang.PersistentQueue/EMPTY)))
