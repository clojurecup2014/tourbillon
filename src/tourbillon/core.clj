(ns tourbillon.core
  (:require [tourbillon.www.core :refer [new-server]]
            [tourbillon.event.store :refer [new-store]]
            [tourbillon.storage.object :refer [new-object-store]]
            [tourbillon.schedule.core :refer [new-scheduler]]
            [tourbillon.workflow.jobs :refer [map->Job map->Workflow]]
            [overtone.at-at :refer [mk-pool]]
            [com.stuartsierra.component :as component]))

(defn system [config-options]
  (let [{:keys [webserver ip port]} config-options]
    (component/system-map
      :config-options config-options
      :webserver (if webserver (new-server ip port) nil)
      :job-store (new-object-store {:type :local
                                    :db (atom {})
                                    :serialize-fn (partial into {})
                                    :unserialize-fn map->Job})
      :workflow-store (new-object-store {:type :local
                                         :db (atom {})
                                         :serialize-fn (partial into {})
                                         :unserialize-fn map->Workflow})
      :event-store (new-store (atom {}))
      :scheduler (component/using
                   (new-scheduler 1000 (mk-pool))
                   [:job-store :event-store]))))
