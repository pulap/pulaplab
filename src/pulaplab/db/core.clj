(ns pulaplab.db.core
  (:require [next.jdbc :as jdbc]
            [pulaplab.config :as config]))

(def db-spec (:db-spec (config/get-config :dev))) ;; Default to :dev, can be overridden in tests

(def datasource (jdbc/get-datasource db-spec))

(defn test-connection []
  (let [result (jdbc/execute! datasource ["SELECT 1"])]
    (if (= result [{:1 1}])
      "Database connection successful!"
      "Database connection failed!")))
