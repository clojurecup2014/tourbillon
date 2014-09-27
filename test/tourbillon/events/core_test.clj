(ns tourbillon.events.core-test
  (:require [clojure.test :refer :all]
            [tourbillon.events.core :refer :all]))

(deftest test-event-record
  (testing "Can be non-recurring"
    (let [event (create-event 5 {})]
      (is (not (is-recurring? event)))))

  (testing "Can be recurring"
    (let [event (create-event 5 10 {})]
      (is (is-recurring? event))))

  (testing "Recuring events can get their next interval"
    (let [event (create-event 5 10 {})
          next-event (next-interval event)]
      (is (= 15 (:start next-event)))
      (is (= 10 (:interval next-event))))))
