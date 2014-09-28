(ns tourbillon.schedule.queue-test
  (:require [clojure.test :refer :all]
            [tourbillon.schedule.queue :refer :all]
            [tourbillon.event.core :refer [create-event]]))

(deftest enqueue-jobs
  (let [event (create-event "event-id" :job-id {})
        queue (new-queue)]

    (testing "enqueue/dequeues single event"
      (enqueue! queue event)
      (is (= event (dequeue! queue))))

    (testing "enqueue/dequeues multiple events in correct order"
      (let [event2 (create-event "event2" :job-id {})]
        (enqueue! queue event)
        (enqueue! queue event2)
        (is (= event (dequeue! queue)))
        (is (= event2 (dequeue! queue)))))))
