(ns auth.role-test
  (:require [clojure.test :refer :all]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [pulaplab.db.core :as db]
            [pulaplab.auth.db :as auth-db]))

(defn setup-test-db []
  ;; Setup the test database for roles without relying on migrations
  (jdbc/execute! db/datasource ["CREATE TABLE IF NOT EXISTS roles (
                                  id TEXT PRIMARY KEY,
                                  slug TEXT,
                                  name TEXT,
                                  description TEXT,
                                  created_at TIMESTAMP,
                                  updated_at TIMESTAMP)"])
  (jdbc/execute! db/datasource ["DELETE FROM roles"])
  (jdbc/execute! db/datasource ["INSERT INTO roles (id, slug, name, description, created_at, updated_at) VALUES
                                ('1', 'admin', 'Administrator', 'Admin role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                ('2', 'user', 'User', 'User role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"]))

(defn teardown-test-db []
  ;; Teardown the test database for roles
  (jdbc/execute! db/datasource ["DELETE FROM roles"]))

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

(deftest test-read-role
  (run-test-with-reporting "test-read-role"
                           (fn []
                             (let [role (auth-db/get-role-by-id "1")]
                               (is (= "Administrator" (:name role)))
                               (is (= "Admin role" (:description role)))))))

(deftest test-create-role
  (run-test-with-reporting "test-create-role"
                           (fn []
                             (auth-db/create-role! {:slug "editor" :name "Editor" :description "Editor role"})
                             (let [roles (auth-db/list-roles)]
                               (is (= 3 (count roles)))
                               (is (some #(= "Editor" (:name %)) roles))))))

(deftest test-update-role
  (run-test-with-reporting "test-update-role"
                           (fn []
                             (auth-db/update-role! "1" {:slug "superadmin" :name "Super Admin" :description "Updated admin role"})
                             (let [role (auth-db/get-role-by-id "1")]
                               (is (= "Super Admin" (:name role)))
                               (is (= "Updated admin role" (:description role)))))))

(deftest test-delete-role
  (run-test-with-reporting "test-delete-role"
                           (fn []
                             (auth-db/delete-role! "1")
                             (let [roles (auth-db/list-roles)]
                               (is (= 1 (count roles)))
                               (is (nil? (some #(= "1" (:id %)) roles)))))))