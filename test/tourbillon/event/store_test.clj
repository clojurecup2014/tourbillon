(ns tourbillon.event.store-test
  (:require [clojure.test :refer :all]
            [tourbillon.event.core :refer :all]
            [tourbillon.event.store :refer :all]))

(def event (create-event "event-id" :job-id 123 {}))

(def ^:dynamic *store*)

(defn with-store [test-fn]
  (let [store (new-store (atom {}))]
    (.start store)
    (binding [*store* store]
      (test-fn))
    (.stop store)))

(use-fixtures :each with-store)

(deftest test-getting-single-event
  (testing "Gets a single event at the correct timestamp"
    (store-event *store* event)
    (let [found-events (get-events *store* 123)]
      (is (= 1 (count found-events)))
      (is (= event (first found-events)))
      (is (empty? (get-events *store* 456)))))

  (testing "Removes an event once it is taken out"))
