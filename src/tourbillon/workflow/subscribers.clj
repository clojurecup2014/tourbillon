(ns tourbillon.workflow.subscribers
  (:require [taoensso.timbre :as log]))

(defmulti notify! (fn [options _]
                    (:type options)))

(defmethod notify! :logger
  [_ data]
  (log/info data))

(defmethod notify! :webhook
  [{:keys [url]} data]
  (log/info (str "calling URL: " url " with " data)))

(defmethod notify! :email
  [{:keys [recipient subject]} data]
  (log/info (str "emailing <" recipient ">: " subject ". DATA: " data)))

(defn notify-all! [subscribers data]
  (doseq [subscriber subscribers]
    (notify! subscriber data)))
