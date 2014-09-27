(ns tourbillon.event.core-test
  (:require [clojure.test :refer :all]
            [tourbillon.event.core :refer :all]))

(deftest test-event-record
  (let [e-immediate (create-event "event-id" :job-id {})
        e-scheduled (create-event "event-id" :job-id 5 {})
        e-recurring (create-event "event-id" :job-id 5 10 {})]

    (testing "Can be immediate"
      (is (is-immediate? e-immediate))
      (is (not (is-immediate? e-scheduled)))
      (is (not (is-immediate? e-recurring))))

    (testing "Can be scheduled for a future time"
      (is (is-future? e-scheduled))
      (is (not (is-future? e-immediate)))
      (is (not (is-future? e-recurring))))

    (testing "Can be recurring"
      (is (is-recurring? e-recurring))
      (is (not (is-recurring? e-scheduled)))
      (is (not (is-recurring? e-immediate))))

    (testing "Recuring events can get their next interval"
      (let [next-event (next-interval e-recurring)]
        (is (= 15 (:start next-event)))
        (is (= 10 (:interval next-event)))))))
