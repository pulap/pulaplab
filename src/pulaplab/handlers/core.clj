(ns pulaplab.handlers.core
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
   ["/auth/list-users"   {:get  auth-web/list-users-handler}]
   ["/auth/new-user"     {:get  auth-web/new-user-handler}]
   ["/auth/create-user"  {:post auth-web/create-user-handler}]
   ["/auth/show-user"    {:get  auth-web/show-user-handler}]
   ["/auth/edit-user"    {:get  auth-web/edit-user-handler}]
   ["/auth/update-user"  {:post auth-web/update-user-handler}]
   ["/auth/delete-user"  {:post auth-web/delete-user-handler}]
   ;; Roles
   ["/auth/list-roles"   {:get  auth-web/list-roles-handler}]
   ["/auth/new-role"     {:get  auth-web/new-role-handler}]
   ["/auth/create-role"  {:post auth-web/create-role-handler}]
   ["/auth/show-role"    {:get  auth-web/show-role-handler}]
   ["/auth/edit-role"    {:get  auth-web/edit-role-handler}]
   ["/auth/update-role"  {:post auth-web/update-role-handler}]
   ["/auth/delete-role"  {:post auth-web/delete-role-handler}]
   ;; Permissions
   ["/auth/list-permissions"   {:get  auth-web/list-permissions-handler}]
   ["/auth/new-permission"     {:get  auth-web/new-permission-handler}]
   ["/auth/create-permission"  {:post auth-web/create-permission-handler}]
   ["/auth/show-permission"    {:get  auth-web/show-permission-handler}]
   ["/auth/edit-permission"    {:get  auth-web/edit-permission-handler}]
   ["/auth/update-permission"  {:post auth-web/update-permission-handler}]
   ["/auth/delete-permission"  {:post auth-web/delete-permission-handler}]
   ;; Resources
   ["/auth/list-resources"   {:get  auth-web/list-resources-handler}]
   ["/auth/new-resource"     {:get  auth-web/new-resource-handler}]
   ["/auth/create-resource"  {:post auth-web/create-resource-handler}]
   ["/auth/show-resource"    {:get  auth-web/show-resource-handler}]
   ["/auth/edit-resource"    {:get  auth-web/edit-resource-handler}]
   ["/auth/update-resource"  {:post auth-web/update-resource-handler}]
   ["/auth/delete-resource"  {:post auth-web/delete-resource-handler}]
   ;; User roles
   ["/auth/list-user-roles" {:get auth-web/list-user-roles-handler}]
   ["/auth/assign-role-to-user" {:post auth-web/assign-role-handler}]
   ["/auth/unassign-role-from-user" {:post auth-web/unassign-role-handler}]
   ;; User permissions
   ["/auth/list-user-permissions" {:get auth-web/list-user-permissions-handler}]
   ["/auth/assign-permission-to-user" {:post auth-web/assign-permission-handler}]
   ["/auth/unassign-permission-from-user" {:post auth-web/unassign-permission-handler}]])


(def app
  (-> (ring/ring-handler
       (ring/router
        [["" {:middleware []}
          public-routes
          ["/private" private-routes]]])
       (ring/create-default-handler
        {:not-found not-found-handler}))
      (wrap-defaults site-defaults)))
