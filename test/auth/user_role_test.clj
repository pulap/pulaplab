(ns auth.user-role-test
  (:require [clojure.test :refer :all]
            [next.jdbc :as jdbc]
            [pulaplab.db.core :as db]
            [pulaplab.auth.db :as auth-db]
            [next.jdbc.result-set :as rs]))

(defn setup-test-db []
  ;; Setup the test database for user-role relationships
  (jdbc/execute! db/datasource ["CREATE TABLE IF NOT EXISTS user_roles (
                                  user_id TEXT,
                                  role_id TEXT,
                                  PRIMARY KEY (user_id, role_id))"])
  (jdbc/execute! db/datasource ["CREATE TABLE IF NOT EXISTS users (
                                  id TEXT PRIMARY KEY,
                                  username TEXT,
                                  email TEXT)"])
  (jdbc/execute! db/datasource ["CREATE TABLE IF NOT EXISTS roles (
                                  id TEXT PRIMARY KEY,
                                  name TEXT)"])
  (jdbc/execute! db/datasource ["DELETE FROM user_roles"])
  (jdbc/execute! db/datasource ["DELETE FROM users"])
  (jdbc/execute! db/datasource ["DELETE FROM roles"])
  (jdbc/execute! db/datasource ["INSERT INTO users (id, username, email) VALUES
                                ('1', 'testuser', 'testuser@example.com')"])
  (jdbc/execute! db/datasource ["INSERT INTO roles (id, name) VALUES
                                ('1', 'Admin'),
                                ('2', 'Editor')"]))

(defn teardown-test-db []
  ;; Teardown the test database for user-role relationships
  (jdbc/execute! db/datasource ["DROP TABLE IF EXISTS user_roles"])
  (jdbc/execute! db/datasource ["DROP TABLE IF EXISTS users"])
  (jdbc/execute! db/datasource ["DROP TABLE IF EXISTS roles"]))

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

(deftest test-assign-role-to-user
  (run-test-with-reporting "test-assign-role-to-user"
                           (fn []
                             (jdbc/execute! db/datasource ["INSERT INTO user_roles (user_id, role_id) VALUES ('1', '1')"])
                             (let [user-roles (jdbc/execute! db/datasource ["SELECT * FROM user_roles"] {:builder-fn rs/as-unqualified-lower-maps})]
                               (is (= 1 (count user-roles)))
                               (is (= "1" (:role_id (first user-roles))))))))

(deftest test-remove-role-from-user
  (run-test-with-reporting "test-remove-role-from-user"
                           (fn []
                             (jdbc/execute! db/datasource ["INSERT INTO user_roles (user_id, role_id) VALUES ('1', '1')"])
                             (jdbc/execute! db/datasource ["DELETE FROM user_roles WHERE user_id = '1' AND role_id = '1'"])
                             (let [user-roles (jdbc/execute! db/datasource ["SELECT * FROM user_roles WHERE user_id = '1'"])]
                               (is (empty? user-roles))))))