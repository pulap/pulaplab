(ns pulaplab.handlers.web.auth
  (:require [pulaplab.auth.ui.pages :as pages]
            [pulaplab.auth.ui.fake :as fake]))

(defn list-users-handler [_request]
  (let [users (fake/fake-users)]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (pages/list-users-page users nil)}))
