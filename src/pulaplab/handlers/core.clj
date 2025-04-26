(ns pulaplab.handlers.core
  (:require [reitit.ring :as ring]
            [ring.middleware.params :refer [wrap-params]]
            [pulaplab.auth.handlers :as auth-web]))

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
                      :body "Admin dashboard (TODO auth)"})}]
   ["/auth/list-users" {:get auth-web/list-users-handler}]
   ["/auth/new-user" {:get auth-web/new-user-handler}]
   ["/auth/create-user" {:post auth-web/create-user-handler}]
   ["/auth/show-user" {:get auth-web/show-user-handler}]
   ["/auth/edit-user" {:get auth-web/edit-user-handler}]
   ["/auth/update-user" {:post auth-web/update-user-handler}]
   ["/auth/delete-user" {:post auth-web/delete-user-handler}] ;; <-- agregar esta línea
   ])


(def app
  (ring/ring-handler
   (ring/router
    [["" {:middleware [wrap-params]}
      public-routes
      ["/private" private-routes]]])
   (ring/create-default-handler
    {:not-found not-found-handler})))
