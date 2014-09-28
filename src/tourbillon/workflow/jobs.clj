(ns tourbillon.workflow.jobs
  (:require [tourbillon.workflow.subscribers :as subscribers]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]))

;; TODO: move into a "store" namespace
(defprotocol JobStore
  (find-job [this id])
  (save-job! [this job])
  (update-job! [this job fun]))

(defrecord Transition [from to on subscribers])
(defn create-transition
  ([from to on] (create-transition from to on []))
  ([from to on subscribers] (Transition. from to on subscribers)))

(defrecord Job [id transitions current-state])
(defn create-job [id transitions current-state]
  (Job. id transitions current-state))

(defn get-valid-transition [job event]
  (let [transitions (:transitions job)
        current-state (:current-state job)]
    (first
      (filter #(and (= (:on %) event)
                    (= (:from %) current-state))
              transitions))))

(defn receive-event! [jobstore job event]
  (if-let [transition (get-valid-transition job event)]
    (let [new-job (update-job! jobstore job #(assoc % :current-state (:to transition)))]
      (subscribers/notify-all! (:subscribers transition))
      new-job)
    job))

(defrecord InMemoryJobStore [db autoincrement]
  component/Lifecycle
  (start [component]
    (log/info "Starting in-memory job store")
    (assoc component :db db
                     :autoincrement autoincrement))

  (stop [component]
    (log/info "Stopping in-memory job store")
    (dissoc component :db :autoincrement))

  JobStore
  (find-job [this id]
    (-> @db
      (get id)
      (map->Job)))

  (save-job! [this job]
    (let [autoincrement (:autoincrement this)
          job-id (swap! autoincrement inc)
          job (assoc job :id job-id)]
      (swap! db assoc job-id (into {} job))
      job))

  (update-job! [this job fun]
    (let [job-id (:id job)]
      (swap! db update-in [job-id] fun)
      (find-job this job-id))))

(defmulti new-jobstore :type)

(defmethod new-jobstore :local [_]
  (map->InMemoryJobStore {:db (atom {})
                          :autoincrement (atom 0)}))
