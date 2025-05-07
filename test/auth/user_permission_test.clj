(ns auth.user-permission-test
  (:require [clojure.test :refer :all]
            [next.jdbc :as jdbc]
            [pulaplab.db.core :as db]
            [pulaplab.auth.db :as auth-db]
            [next.jdbc.result-set :as rs]))

(defn setup-test-db []
  ;; Setup the test database for user-permission relationships
  (jdbc/execute! db/datasource ["CREATE TABLE IF NOT EXISTS user_permissions (
                                  user_id TEXT,
                                  permission_id TEXT,
                                  PRIMARY KEY (user_id, permission_id))"])
  (jdbc/execute! db/datasource ["CREATE TABLE IF NOT EXISTS users (
                                  id TEXT PRIMARY KEY,
                                  username TEXT,
                                  email TEXT)"])
  (jdbc/execute! db/datasource ["CREATE TABLE IF NOT EXISTS permissions (
                                  id TEXT PRIMARY KEY,
                                  name TEXT)"])
  (jdbc/execute! db/datasource ["DELETE FROM user_permissions"])
  (jdbc/execute! db/datasource ["DELETE FROM users"])
  (jdbc/execute! db/datasource ["DELETE FROM permissions"])
  (jdbc/execute! db/datasource ["INSERT INTO users (id, username, email) VALUES
                                ('1', 'testuser', 'testuser@example.com')"])
  (jdbc/execute! db/datasource ["INSERT INTO permissions (id, name) VALUES
                                ('1', 'Read'),
                                ('2', 'Write')"]))

(defn teardown-test-db []
  ;; Teardown the test database for user-permission relationships
  (jdbc/execute! db/datasource ["DROP TABLE IF EXISTS user_permissions"])
  (jdbc/execute! db/datasource ["DROP TABLE IF EXISTS users"])
  (jdbc/execute! db/datasource ["DROP TABLE IF EXISTS permissions"]))

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

(deftest test-assign-permission-to-user
  (run-test-with-reporting "test-assign-permission-to-user"
                           (fn []
                             (jdbc/execute! db/datasource ["INSERT INTO user_permissions (user_id, permission_id) VALUES ('1', '1')"])
                             (let [user-permissions (jdbc/execute! db/datasource ["SELECT * FROM user_permissions"] {:builder-fn rs/as-unqualified-lower-maps})]
                               (is (= 1 (count user-permissions)))
                               (is (= "1" (:permission_id (first user-permissions))))))))

(deftest test-remove-permission-from-user
  (run-test-with-reporting "test-remove-permission-from-user"
                           (fn []
                             (jdbc/execute! db/datasource ["INSERT INTO user_permissions (user_id, permission_id) VALUES ('1', '1')"])
                             (jdbc/execute! db/datasource ["DELETE FROM user_permissions WHERE user_id = '1' AND permission_id = '1'"])
                             (let [user-permissions (jdbc/execute! db/datasource ["SELECT * FROM user_permissions WHERE user_id = '1'"])]
                               (is (empty? user-permissions))))))