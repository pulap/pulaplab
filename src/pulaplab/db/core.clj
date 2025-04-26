(ns pulaplab.db.core
  (:require [next.jdbc :as jdbc]))

(def db-spec
  {:dbtype "sqlite"
   :dbname "pulaplab.db"})

(def datasource (jdbc/get-datasource db-spec))

(defn test-connection []
  (let [result (jdbc/execute! datasource ["SELECT 1"])]
    (if (= result [{:1 1}])
      "Database connection successful!"
      "Database connection failed!")))
