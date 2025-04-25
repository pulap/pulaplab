(ns pulaplab.db.core
  (:require [next.jdbc :as jdbc]))

(def db-spec
  {:dbtype "sqlite"
   :dbname "pulaplab.db"})

(def datasource (jdbc/get-datasource db-spec))

(defn test-connection []
  (jdbc/execute! datasource ["SELECT 1"]))
