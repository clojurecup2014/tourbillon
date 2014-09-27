(ns tourbillon.schedule.core
  (:require [tourbillon.event.core :refer :all]
            [tourbillon.schedule.queue :refer [make-queue enqueue dequeue]]
            [com.stuartsierra.component :as component]
            [overtone.at-at :refer :all]
            [taoensso.timbre :as log]))

;; (declare schedule new-scheduler)

;; (def poll-freq 1000)

;; (defmulti send-event
;;   (fn [event _]
;;     (if (is-future? event)
;;       :delayed
;;       :immediate)))

;; (defmethod send-event :immediate [event queue]
;;   (enqueue event))

;; (defmethod send-event :delayed [event queue]
;;   ())

;; (defn process-events
;;   "Gets any new events from the queue and sends them off to update their jobs"
;;   [])

;; (defn start [queue pool]
;;   (every poll-freq #(process-events queue) pool))

;; (defrecord Scheduler [queue thread-pool scheduler]
;;   component/Lifecycle

;;   (start [component]
;;     (log/info "Starting scheduler")
;;     (let [sched (start queue thread-pool)]
;;       (assoc component :scheduler sched)))

;;   (stop [component]
;;     (log/info "Stopping scheduler")
;;         (scheduler)
;;         (dissoc component :scheduler)))

;; (defn new-scheduler [queue thread-pool]
;;   (map->Scheduler {:queue queue :thread-pool thread-pool}))
