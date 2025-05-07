(ns auth.resource-test
  (:require [clojure.test :refer :all]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [pulaplab.db.core :as db]
            [pulaplab.auth.db :as auth-db]))

(defn setup-test-db []
  ;; Setup the test database for resources
  (jdbc/execute! db/datasource ["CREATE TABLE IF NOT EXISTS resources (
                                  id TEXT PRIMARY KEY,
                                  slug TEXT,
                                  name TEXT,
                                  description TEXT,
                                  created_at TIMESTAMP,
                                  updated_at TIMESTAMP)"])
  (jdbc/execute! db/datasource ["DELETE FROM resources"])
  (jdbc/execute! db/datasource ["INSERT INTO resources (id, slug, name, description, created_at, updated_at) VALUES
                                ('1', 'dashboard', 'Dashboard', 'Main dashboard resource', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                ('2', 'reports', 'Reports', 'Reports resource', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"]))

(defn teardown-test-db []
  ;; Teardown the test database for resources
  (jdbc/execute! db/datasource ["DELETE FROM resources"]))

(use-fixtures :each
  (fn [test-fn]
    (setup-test-db)
    (try
      (test-fn)
      (finally
        (teardown-test-db)))))

(defn run-test-with-reporting [test-name test-fn]
  (print (str test-name ": "))
  (let [initial-report-counters (atom nil)
        success (atom true)]
    (binding [clojure.test/*report-counters* (ref (assoc (merge {} @clojure.test/*report-counters*) :fail 0 :error 0))]
      (reset! initial-report-counters @clojure.test/*report-counters*)
      (try
        (test-fn)
        (finally
          ;; Check if any failures or errors occurred by comparing counters
          (when (or (> (:fail @clojure.test/*report-counters*) 0)
                    (> (:error @clojure.test/*report-counters*) 0))
            (reset! success false))
          (if @success
            (println "✅")
            (println (str "❌ Test failed for " test-name))))))))

(deftest test-read-resource
  (run-test-with-reporting "test-read-resource"
                           (fn []
                             (let [resource (auth-db/get-resource-by-id "1")]
                               (is (= "Dashboard" (:name resource)))
                               (is (= "Main dashboard resource" (:description resource)))))))

(deftest test-create-resource
  (run-test-with-reporting "test-create-resource"
                           (fn []
                             (auth-db/create-resource! {:slug "settings" :name "Settings" :description "Settings resource"})
                             (let [resources (auth-db/list-resources)]
                               (is (= 3 (count resources)))
                               (is (some #(= "Settings" (:name %)) resources))))))

(deftest test-update-resource
  (run-test-with-reporting "test-update-resource"
                           (fn []
                             (auth-db/update-resource! "1" {:slug "main-dashboard" :name "Main Dashboard" :description "Updated dashboard resource"})
                             (let [resource (auth-db/get-resource-by-id "1")]
                               (is (= "Main Dashboard" (:name resource)))
                               (is (= "Updated dashboard resource" (:description resource)))))))

(deftest test-delete-resource
  (run-test-with-reporting "test-delete-resource"
                           (fn []
                             (auth-db/delete-resource! "1")
                             (let [resources (auth-db/list-resources)]
                               (is (= 1 (count resources)))
                               (is (nil? (some #(= "1" (:id %)) resources)))))))