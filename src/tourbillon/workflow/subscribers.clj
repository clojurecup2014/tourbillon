(ns tourbillon.workflow.subscribers
  (:require [org.httpkit.client :as http]
            [taoensso.timbre :as log]
            [clojure.string :refer [lower-case]]))

(defmulti notify! (fn [options _]
                    (keyword (:type options))))

(defmethod notify! :logger
  [_ data]
  (log/info data))

(defmethod notify! :webhook
  [{:keys [url method]} data]
  (do
    (log/info (str method " : " url " : " data))
    (if (= (lower-case method) "get")
      (http/get url {:query-params data})
      (http/post url {:body data}))))

(defmethod notify! :email
  [{:keys [recipient subject]} data]
  (log/info (str "emailing <" recipient ">: " subject ". DATA: " data)))

(defn notify-all! [subscribers data]
  (doseq [subscriber subscribers]
    (notify! subscriber data)))
