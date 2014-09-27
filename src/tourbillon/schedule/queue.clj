(ns tourbillon.schedule.queue)

(defn make-queue []
  (atom (clojure.lang.PersistentQueue/EMPTY)))

(defn enqueue [queue item]
  (swap! queue conj item))

(defn dequeue [queue]
  (when-let [out (peek @queue)]
    (swap! queue pop)
    out))
