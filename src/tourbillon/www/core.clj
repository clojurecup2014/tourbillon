(ns tourbillon.www.core
  (:require [tourbillon.www.api :refer [api-routes]]
            [com.stuartsierra.component :as component]
            [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :refer [content-type
                                        resource-response]]
            [taoensso.timbre :as log]))

(defroutes web
  (GET "/" [] (content-type
                (resource-response "index.html" {:root "public"})
                "text/html"))
  (route/resources "/assets")
  (context "/api" [] api-routes)
  (route/not-found "<h2>Still haven't found what you're lookin' for?</h2>"))

(def app
  (handler/site web))

(defrecord Webserver [ip port connection]
  component/Lifecycle

  (start [component]
    (log/info "Starting web server")

    (let [conn (server/run-server #'app {:ip ip :port port})]
      (assoc component :connection conn)))

  (stop [component]
    (log/info "Stopping web server")

        (connection)
        (dissoc component :connection)))

(defn new-server [ip port]
  (map->Webserver {:ip ip :port port}))
