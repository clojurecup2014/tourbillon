(ns tourbillon.storage.object
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]))

(defprotocol ObjectStore
  (find-by-id [this id])
  (save! [this obj])
  (update! [this obj fun]))

(defrecord InMemoryObjectStore [db serialize-fn unserialize-fn autoincrement]
  component/Lifecycle
  (start [component]
    (log/info "Starting in-memory object store")
    (assoc component :db db))

  (stop [component]
    (log/info "Stopping in-memory object store")
    (dissoc component :db))

  ObjectStore
  (find-by-id [this id]
    (-> @db
      (get id)
      unserialize-fn))

  (save! [this obj]
    (let [id (swap! autoincrement inc)
          obj (assoc obj :id id)]
      (swap! db assoc id (serialize-fn obj))
      obj))

  (update! [this obj fun]
    (let [id (:id obj)]
      (swap! db update-in [id] fun)
      (find-by-id this id))))

(defmulti new-object-store :type)

(defmethod new-object-store :local
  [{:keys [db id-prefix serialize-fn unserialize-fn]}]
  (map->InMemoryObjectStore {:db db
                             :serialize-fn serialize-fn
                             :unserialize-fn unserialize-fn
                             :autoincrement (atom 0)}))
