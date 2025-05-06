(ns pulaplab.routes
  (:require
   [ring.util.response :refer [redirect]]
   [reitit.ring :as ring]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [pulaplab.auth.handlers          :as auth-web]))

(defn not-found-handler
  "Return a 404 response when no route matches."
  [_]
  {:status  404
   :headers {"Content-Type" "text/plain"}
   :body    "Not found"})

(def public-routes
  [;; NOTE: edirecting to list users for now
   ;; ["/" {:get (fn [_]
   ;;               {:status  200
   ;;                :headers {"Content-Type" "text/plain"}
   ;;                :body    "Welcome to Pulap!"})}]
   ["/" {:get (fn [_]
                (redirect "/private/auth/list-users"))}]
   ["/health" {:get (fn [_]
                      {:status  200
                       :headers {"Content-Type" "text/plain"}
                       :body    "OK"})}]])

(def private-routes
  [["/admin" {:get (fn [_]
                     {:status  200
                      :headers {"Content-Type" "text/plain"}
                      :body    "Admin dashboard (TODO auth)"})}]
   ;; Users
   ["/auth/list-users"   {:get  auth-web/list-users}]
   ["/auth/new-user"     {:get  auth-web/new-user}]
   ["/auth/create-user"  {:post auth-web/create-user}]
   ["/auth/show-user"    {:get  auth-web/show-user}]
   ["/auth/edit-user"    {:get  auth-web/edit-user}]
   ["/auth/update-user"  {:post auth-web/update-user}]
   ["/auth/delete-user"  {:post auth-web/delete-user}]
   ;; Roles
   ["/auth/list-roles"   {:get  auth-web/list-roles}]
   ["/auth/new-role"     {:get  auth-web/new-role}]
   ["/auth/create-role"  {:post auth-web/create-role}]
   ["/auth/show-role"    {:get  auth-web/show-role}]
   ["/auth/edit-role"    {:get  auth-web/edit-role}]
   ["/auth/update-role"  {:post auth-web/update-role}]
   ["/auth/delete-role"  {:post auth-web/delete-role}]
   ;; Permissions
   ["/auth/list-permissions"   {:get  auth-web/list-permissions}]
   ["/auth/new-permission"     {:get  auth-web/new-permission}]
   ["/auth/create-permission"  {:post auth-web/create-permission}]
   ["/auth/show-permission"    {:get  auth-web/show-permission}]
   ["/auth/edit-permission"    {:get  auth-web/edit-permission}]
   ["/auth/update-permission"  {:post auth-web/update-permission}]
   ["/auth/delete-permission"  {:post auth-web/delete-permission}]
   ;; Resources
   ["/auth/list-resources"   {:get  auth-web/list-resources}]
   ["/auth/new-resource"     {:get  auth-web/new-resource}]
   ["/auth/create-resource"  {:post auth-web/create-resource}]
   ["/auth/show-resource"    {:get  auth-web/show-resource}]
   ["/auth/edit-resource"    {:get  auth-web/edit-resource}]
   ["/auth/update-resource"  {:post auth-web/update-resource}]
   ["/auth/delete-resource"  {:post auth-web/delete-resource}]
   ;; User roles
   ["/auth/list-user-roles" {:get auth-web/list-user-roles}]
   ["/auth/assign-role-to-user" {:post auth-web/assign-role}]
   ["/auth/unassign-role-from-user" {:post auth-web/unassign-role}]
   ;; User permissions
   ["/auth/list-user-permissions" {:get auth-web/list-user-permissions}]
   ["/auth/assign-permission-to-user" {:post auth-web/assign-permission}]
   ["/auth/unassign-permission-from-user" {:post auth-web/unassign-permission}]
   ;; Role permissions
   ["/auth/list-role-permissions" {:get auth-web/list-role-permissions}]
   ["/auth/assign-permission-to-role" {:post auth-web/assign-permission-to-role}]
   ["/auth/unassign-permission-from-role" {:post auth-web/unassign-permission-from-role}]
   ;; Resource permissions
   ["/auth/list-resource-permissions" {:get auth-web/list-resource-permissions}]
   ["/auth/assign-permission-to-resource" {:post auth-web/assign-permission-to-resource}]
   ["/auth/unassign-permission-from-resource" {:post auth-web/unassign-permission-from-resource}]])


(def app
  (-> (ring/ring-handler
       (ring/router
        [["" {:middleware []}
          public-routes
          ["/private" private-routes]]])
       (ring/create-default-handler
        {:not-found not-found-handler}))
      (wrap-defaults site-defaults)))
