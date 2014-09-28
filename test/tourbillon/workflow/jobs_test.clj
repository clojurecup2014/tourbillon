(ns tourbillon.workflow.jobs-test
  (:require [clojure.test :refer :all]
            [tourbillon.workflow.jobs :refer :all]))

(def ^:dynamic *jobstore*)

(defn with-local-jobstore [test-fn]
  (let [jobstore (new-jobstore {:type :local})]
    (.start jobstore)
    (binding [*jobstore* jobstore]
      (test-fn))
    (.stop jobstore)))

(use-fixtures :each with-local-jobstore)

(defn mk-test-event [id job]
  {:id id
   :job-id (:id job)
   :data {}})

(deftest test-local-jobstore
  (testing "Assigns id to job on save"
    (let [job (create-job nil [] nil)
          saved-job (save-job! *jobstore* job)]
      (is (not (nil? (:id saved-job))))))

  (testing "Can retrieve saved job"
    (let [saved-job (save-job! *jobstore* (create-job nil [] nil))
          retrieved-job (find-job *jobstore* (:id saved-job))]
      (is (= saved-job retrieved-job))))

  (testing "Updates existing job with function"
    (let [saved-job (save-job! *jobstore* (create-job nil [] nil))
          updated-job (update-job! *jobstore* saved-job
                                              #(update-in % [:states] conj :foo))
          retrieved-job (find-job *jobstore* (:id saved-job))]
      
      (is (= updated-job retrieved-job))
      (is (= :foo (-> updated-job :states first))))))

(deftest test-state-transitioning
  (let [transitions [(create-transition :foo :bar "foo->bar")
                     (create-transition :bar :baz "bar->baz")]
        job (save-job! *jobstore* (create-job nil transitions :foo))]

    (testing "does not change state when event does not match transition"
      (let [new-job (emit! *jobstore* (mk-test-event "bar->baz" job))]
        (is (= :foo (:current-state new-job)))))

    (testing "changes states when event matches transition"
      (let [new-job (emit! *jobstore* (mk-test-event "foo->bar" job))]
        (is (= :bar (:current-state new-job)))))))
