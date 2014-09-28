(ns tourbillon.workflow.jobs-test
  (:require [clojure.test :refer :all]
            [tourbillon.workflow.jobs :refer :all]
            [tourbillon.storage.object :refer [new-object-store find-by-id save! update!]]))

(def ^:dynamic *jobstore*)
(def ^:dynamic *workflowstore*)

(defn with-local-storage [test-fn]
  (let [jobstore (new-object-store {:type :local
                                    :db (atom {})
                                    :serialize-fn (partial into {})
                                    :unserialize-fn map->Job})
        workflowstore (new-object-store {:type :local
                                         :db (atom {})
                                         :serialize-fn (partial into {})
                                         :unserialize-fn map->Workflow})]
    (.start jobstore)
    (.start workflowstore)
    (binding [*jobstore* jobstore
              *workflowstore* workflowstore]
      (test-fn))
    (.stop workflowstore)
    (.stop jobstore)))

(use-fixtures :each with-local-storage)

(defn mk-test-event [id job]
  {:id id
   :job-id (:id job)
   :data {}})

(deftest test-local-jobstore
  (testing "Assigns id to job on save"
    (let [job (create-job nil [] nil)
          saved-job (save! *jobstore* job)]
      (is (not (nil? (:id saved-job))))))

  (testing "Can retrieve saved job"
    (let [saved-job (save! *jobstore* (create-job nil [] nil))
          retrieved-job (find-by-id *jobstore* (:id saved-job))]
      (is (= saved-job retrieved-job))))

  (testing "Updates existing job with function"
    (let [saved-job (save! *jobstore* (create-job nil [] nil))
          updated-job (update! *jobstore* saved-job
                                              #(update-in % [:states] conj :foo))
          retrieved-job (find-by-id *jobstore* (:id saved-job))]
      
      (is (= updated-job retrieved-job))
      (is (= :foo (-> updated-job :states first))))))

(deftest test-creating-jobs-from-workflows
  (testing "Creates job using workflow as blueprint"
    (let [workflow (save! *workflowstore* (create-workflow nil [] nil))
          job (Workflow->Job workflow)]
      (is (nil? (:id job)))
      (is (not= job workflow)))))

(deftest test-state-transitioning
  (let [transitions [(create-transition :foo :bar "foo->bar")
                     (create-transition :bar :baz "bar->baz")]
        job (save! *jobstore* (create-job nil transitions :foo))]

    (testing "does not change state when event does not match transition"
      (let [new-job (emit! *jobstore* (mk-test-event "bar->baz" job))]
        (is (= :foo (:current-state new-job)))))

    (testing "changes states when event matches transition"
      (let [new-job (emit! *jobstore* (mk-test-event "foo->bar" job))]
        (is (= :bar (:current-state new-job)))))))
