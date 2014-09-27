(ns user
  (:require [tourbillon.core :as core]
            [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer [refresh]]))

(def system nil)

(defn init []
  (alter-var-root #'system
    (constantly (core/system {:workers 1
                              :webserver true
                              :ip "0.0.0.0"
                              :port 3000}))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
    (fn [s] (when s (component/stop s)))))

(defn go []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))
