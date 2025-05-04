(ns pulaplab.core
  (:require [pulaplab.handlers.core :as handlers]
            [pulaplab.db.core :as db]
            [pulaplab.db.migrations :as migrations]
            [ring.adapter.jetty :as jetty]))

(defonce server (atom nil))

(defn app []
  (require 'pulaplab.handlers.core :reload)
  handlers/app)

(defn stop-server []
  (when @server
    (println "Stopping Pulap HTTP server...")
    (.stop @server)
    (reset! server nil)))

(defn start-server []
  (when @server
    (stop-server))
  (println "Starting Pulap HTTP server...")
  (println "Testing DB connection:" (db/test-connection))
  (migrations/migrate!)
  (let [srv (jetty/run-jetty (app) {:port 3000 :join? false})]
    (reset! server srv)
    (println "Pulap server running on http://localhost:3000")
    srv))

(defn restart-server []
  (stop-server)
  (start-server))

(defn -main []
  (start-server))

;; (stop-server)
;; (start-server)
