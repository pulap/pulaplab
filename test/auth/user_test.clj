(ns auth.user-test
  (:require [clojure.test :refer :all]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs]
            [pulaplab.config :as config]
            [pulaplab.db.core :as db]))

;; Override the global datasource in db.core to use the test environment
(alter-var-root #'db/datasource (constantly (jdbc/get-datasource (:db-spec (config/get-config :test)))))

(def db-spec (:db-spec (config/get-config :test))) ;; Use the test environment configuration

(def datasource (atom nil)) ;; Use an atom to manage the datasource

(defn get-connection []
  (if (nil? @datasource)
    (reset! datasource (jdbc/get-datasource db-spec))
    @datasource))

(defn apply-migrations []
  (let [migration-sql "CREATE TABLE IF NOT EXISTS users (
                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                          username TEXT NOT NULL,
                          email_enc TEXT NOT NULL,
                          password_enc TEXT NOT NULL,
                          slug TEXT,
                          last_login_at TEXT,
                          last_login_ip TEXT,
                          is_active BOOLEAN NOT NULL)"]
    (jdbc/execute! (get-connection) [migration-sql])))

(defn verify-users-table []
  (let [result (jdbc/execute! (get-connection) ["SELECT name FROM sqlite_master WHERE type='table' AND name='users'"])]
    (when (empty? result)
      (throw (Exception. "Users table does not exist!")))))

(defn delete-test-db-file []
  (let [db-file "test-pulaplab.db"]
    (when (.exists (java.io.File. db-file))
      (.delete (java.io.File. db-file)))))

(defn setup-test-db []
  (delete-test-db-file)
  (apply-migrations)
  (verify-users-table)
  (jdbc/execute! (get-connection) ["DELETE FROM users"])
  (jdbc/execute! (get-connection) ["INSERT INTO users (username, email_enc, password_enc, is_active)
                           VALUES ('testuser', 'testemail', 'testpassword', 1)"]))

(defn teardown-test-db []
  (jdbc/execute! (get-connection) ["DROP TABLE IF EXISTS users"]))

(defn query-with-debug [sql & params]
  (jdbc/execute! db/datasource (into [sql] params) {:builder-fn rs/as-unqualified-lower-maps}))

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

(use-fixtures :each
  (fn [test-fn]
    (setup-test-db)
    (try
      (test-fn)
      (finally
        (teardown-test-db)))))

(deftest test-read-user
  (run-test-with-reporting "test-read-user"
                           (fn []
                             (let [users (query-with-debug "SELECT * FROM users WHERE username = ?" "testuser")]
                               (is (= 1 (count users)))
                               (is (= "testuser" (:username (first users))))))))

(deftest test-create-user
  (run-test-with-reporting "test-create-user"
                           (fn []
                             (let [new-user {:username "newuser"
                                             :email_enc "newemail"
                                             :password_enc "newpassword"
                                             :is_active true}]
                               (sql/insert! db/datasource :users new-user)
                               (let [users (query-with-debug "SELECT * FROM users WHERE username = ?" "newuser")]
                                 (is (= 1 (count users)))
                                 (is (= "newuser" (:username (first users)))))))))

(deftest test-update-user
  (run-test-with-reporting "test-update-user"
                           (fn []
                             (sql/update! db/datasource :users {:email_enc "updatedemail"} ["username = ?" "testuser"])
                             (let [users (query-with-debug "SELECT * FROM users WHERE username = ?" "testuser")]
                               (is (= "updatedemail" (:email_enc (first users))))))))

(deftest test-delete-user
  (run-test-with-reporting "test-delete-user"
                           (fn []
                             (sql/delete! db/datasource :users ["username = ?" "testuser"])
                             (let [users (query-with-debug "SELECT * FROM users WHERE username = ?" "testuser")]
                               (is (empty? users))))))