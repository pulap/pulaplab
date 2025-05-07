(ns pulaplab.middleware
  (:require [clojure.tools.logging :as log]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defn wrap-exception-handling
  "Middleware to catch exceptions, log, and redirect to a configurable error page."
  [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable e
        (log/error e (str "Unhandled exception for request: " (:uri req)))
        {:status  302
         :headers {"Location" "/private/auth/list-users"}
         :body    ""}))))

(defn wrap-dev-only
  "Add stacktrace display in dev."
  [handler]
  (wrap-stacktrace handler))