(ns pulaplab.core
  (:require [pulaplab.handlers.core :as handler]
            [pulaplab.db.core :as db]
            [pulaplab.db.migrations :as migrations]
            [ring.adapter.jetty :as jetty]))

(defn -main []
  (println "Starting Pulap HTTP server...")
  (println "Testing DB connection:" (db/test-connection))
  (migrations/migrate!)
  (jetty/run-jetty handler/app {:port 3000 :join? false}))
