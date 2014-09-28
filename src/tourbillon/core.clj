(ns tourbillon.core
  (:require [tourbillon.www.core :refer [new-server]]
            [tourbillon.event.store :refer [new-store]]
            [tourbillon.schedule.core :refer [new-scheduler]]
            [tourbillon.schedule.queue :refer [new-queue]]
            [overtone.at-at :refer [mk-pool]]
            [com.stuartsierra.component :as component]))

(defn system [config-options]
  (let [{:keys [webserver ip port]} config-options]
    (component/system-map
      :config-options config-options
      :webserver (if webserver (new-server ip port) nil)
      :event-store (new-store (atom {}))
      :queue (new-queue)
      :scheduler (component/using
                   (new-scheduler 1000 (mk-pool))
                   [:queue :event-store]))))
