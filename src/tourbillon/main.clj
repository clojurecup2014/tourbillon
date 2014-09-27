(ns tourbillon.main
  (:gen-class)
  (:require [tourbillon.core :refer [system]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as log]
            [environ.core :refer [env]]))

(log/set-config! [:appenders :spit :enabled?] true)
(log/set-config! [:shared-appender-config :spit-filename] (get env :log-file "/var/log/tourbillon.log"))

(defn -main
  "Start application with a given number of worker processes and optionally
  a webserver for a sample client application."
  [& args]
  (let [[workers webserver ip port] args]
    (log/info "Starting system")
    (component/start
     (system {:workers (read-string workers)
              :webserver (= "true" webserver)
              :ip ip
              :port (read-string port)}))))
