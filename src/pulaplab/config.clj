(ns pulaplab.config)

(def config
  {:dev  {:db-spec {:dbtype "sqlite" :dbname (or (System/getenv "PULAP_DB_NAME") "pulaplab.db")}}
   :prod {:db-spec {:dbtype "sqlite" :dbname (or (System/getenv "PULAP_DB_NAME") "pulaplab.db")}}
   :test {:db-spec {:dbtype "sqlite" :dbname "test-pulaplab.db"}}})

(defn get-config [env]
  (get config env))