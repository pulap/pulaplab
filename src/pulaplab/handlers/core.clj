(ns pulaplab.handlers.core
  (:require [reitit.ring :as ring]
            [ring.middleware.params :refer [wrap-params]]))

(defn not-found-handler [_]
  {:status 404
   :headers {"Content-Type" "text/plain"}
   :body "Not found"})

(def public-routes
  [["/" {:get (fn [_]
                {:status 200
                 :headers {"Content-Type" "text/plain"}
                 :body "Welcome to Pulap!"})}]
   ["/health" {:get (fn [_]
                      {:status 200
                       :headers {"Content-Type" "text/plain"}
                       :body "OK"})}]])

(def private-routes
  [["/admin" {:get (fn [_]
                     {:status 200
                      :headers {"Content-Type" "text/plain"}
                      :body "Admin dashboard (TODO auth)"})}]])

(def app
  (ring/ring-handler
   (ring/router
    [["" {:middleware [wrap-params]}
      public-routes
      ["/private" private-routes]]])
   (ring/create-default-handler
    {:not-found not-found-handler})))
