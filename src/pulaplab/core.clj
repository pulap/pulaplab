(ns pulaplab.core
  (:require [pulaplab.routes :as routes]
            [pulaplab.db.core :as db]
            [pulaplab.db.migrations :as migrations]
            [ring.adapter.jetty :as jetty]
            [clojure.tools.logging :as log]))

(defonce server (atom nil))

(defn app []
  (require 'pulaplab.routes :reload)
  routes/app)

(defn stop-server []
  (when @server
    (log/debug "Stopping Pulap HTTP server...")
    (.stop @server)
    (reset! server nil)))

(defn start-server []
  (when @server
    (stop-server))
  (log/debug "Starting Pulap HTTP server...")
  (log/debug "Testing DB connection:" (db/test-connection))
  (migrations/migrate!)
  (let [srv (jetty/run-jetty (app) {:port 3000 :join? false})]
    (reset! server srv)
    (log/debug "Pulap server running on http://localhost:3000")
    srv))

(defn restart-server []
  (stop-server)
  (start-server))

(defn -main []
  (start-server))
