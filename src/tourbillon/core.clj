(ns tourbillon.core
  (:require [tourbillon.www.core :as www]
            [com.stuartsierra.component :as component]))

(defn system [config-options]
  (let [{:keys [workers webserver ip port]} config-options]
    (component/system-map
      :config-options config-options
      :workers workers
      :webserver (if webserver (www/new-server ip port) nil))))
