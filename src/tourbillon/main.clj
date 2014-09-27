(ns tourbillon.main
  (:gen-class)
  (:require [tourbillon.core :refer [system]]
            [com.stuartsierra.component :as component]))

(defn -main
  "Start application with a given number of worker processes and optionally
  a webserver for a sample client application."
  [& args]
  (let [[workers webserver ip port] args]
    (component/start
     (system {:workers (read-string workers)
              :webserver (= "true" webserver)
              :ip ip
              :port (read-string port)}))))
