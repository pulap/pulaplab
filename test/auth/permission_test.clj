(ns auth.permission-test
  (:require [clojure.test :refer :all]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [pulaplab.db.core :as db]
            [pulaplab.auth.db :as auth-db]))

(defn setup-test-db []
  ;; Setup the test database for permissions
  (jdbc/execute! db/datasource ["CREATE TABLE IF NOT EXISTS permissions (
                                  id TEXT PRIMARY KEY,
                                  slug TEXT,
                                  name TEXT,
                                  description TEXT,
                                  created_at TIMESTAMP,
                                  updated_at TIMESTAMP)"])
  (jdbc/execute! db/datasource ["DELETE FROM permissions"])
  (jdbc/execute! db/datasource ["INSERT INTO permissions (id, slug, name, description, created_at, updated_at) VALUES
                                ('1', 'read', 'Read Permission', 'Allows read access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                ('2', 'write', 'Write Permission', 'Allows write access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"]))

(defn teardown-test-db []
  ;; Teardown the test database for permissions
  (jdbc/execute! db/datasource ["DELETE FROM permissions"]))

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

(deftest test-read-permission
  (run-test-with-reporting "test-read-permission"
                           (fn []
                             (let [permission (auth-db/get-permission-by-id "1")]
                               (is (= "Read Permission" (:name permission)))
                               (is (= "Allows read access" (:description permission)))))))

(deftest test-create-permission
  (run-test-with-reporting "test-create-permission"
                           (fn []
                             (auth-db/create-permission! {:slug "delete" :name "Delete Permission" :description "Allows delete access"})
                             (let [permissions (auth-db/list-permissions)]
                               (is (= 3 (count permissions)))
                               (is (some #(= "Delete Permission" (:name %)) permissions))))))

(deftest test-update-permission
  (run-test-with-reporting "test-update-permission"
                           (fn []
                             (auth-db/update-permission! "1" {:slug "read-all" :name "Read All Permission" :description "Allows read access to all"})
                             (let [permission (auth-db/get-permission-by-id "1")]
                               (is (= "Read All Permission" (:name permission)))
                               (is (= "Allows read access to all" (:description permission)))))))

(deftest test-delete-permission
  (run-test-with-reporting "test-delete-permission"
                           (fn []
                             (auth-db/delete-permission! "1")
                             (let [permissions (auth-db/list-permissions)]
                               (is (= 1 (count permissions)))
                               (is (nil? (some #(= "1" (:id %)) permissions)))))))