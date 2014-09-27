(defproject tourbillon "0.1.0-SNAPSHOT"
  :description "web service for managing application workflows"
  :url "http://tourbillon.clojurecup.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :repl-options {:init-ns user}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.stuartsierra/component "0.2.2"]
                 [http-kit "2.1.16"]
                 [com.taoensso/timbre "3.3.1"]
                 [ring-server "0.3.1"]
                 [ring/ring-ssl "0.2.1"]
                 [ring/ring-json "0.3.1"]
                 [compojure "1.1.9"]
                 [cheshire "5.3.1"]
                 [org.clojure/tools.namespace "0.2.7"]
                 [environ "1.0.0"]]

  :main tourbillon.main

  :plugins [[lein-environ "1.0.0"]]

  :profiles {:uberjar {:aot :all},
             :production {:ring {:open-browser? false
                                 :stacktraces? false,
                                 :auto-reload? false}}
             :dev {:dependencies [[ring-mock "0.1.5"]
                                  [ring/ring-devel "1.2.2"]]}})
