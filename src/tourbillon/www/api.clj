(ns tourbillon.www.api
  (:require [compojure.core :refer :all]))

(defroutes api-routes
  (GET "/" [] "here!"))