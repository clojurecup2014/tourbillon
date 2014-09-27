(ns tourbillon.www.core
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]))

(defroutes web
  (GET "/" [] "<h1>Tourbillon. Really cool stuff to come...</h1>")
  (route/not-found "<h2>Still haven't found what you're lookin' for?</h2>"))

(def app
  (handler/site web))

(defrecord Webserver [ip port connection]
  component/Lifecycle

  (start [component]
    (println "Starting web server")

    (let [conn (server/run-server #'app {:ip ip :port port})]
      (assoc component :connection conn)))

  (stop [component]
    (println "Stopping web server")

        (connection)
        (dissoc component :connection)))

(defn new-server [ip port]
  (map->Webserver {:ip ip :port port}))
