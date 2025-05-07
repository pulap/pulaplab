(ns pulaplab.auth.handlers
  (:require [pulaplab.auth.ui.user :as user-pages]
            [pulaplab.auth.ui.role :as role-pages]
            [pulaplab.auth.ui.permission :as permission-pages]
            [pulaplab.auth.ui.resource :as resource-pages]
            [pulaplab.auth.service :as auth-service]
            [hiccup.page :as page]
            [clojure.tools.logging :as log]))

;; -------------------
;; User handlers
;; -------------------

(defn list-users [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (user-pages/index (auth-service/list-users) nil)})

(defn new-user [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (user-pages/new)})

(defn create-user [request]
  (let [{:strs [username email]} (:form-params request)]
    (auth-service/create-user! {:username username :email email})
    {:status 302
     :headers {"Location" "/private/auth/list-users"}
     :body ""}))

(defn show-user [request]
  (let [id (get-in request [:query-params "id"])
        user (auth-service/get-user-by-id id)]
    (if user
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (user-pages/show user)}
      {:status 404
       :headers {"Content-Type" "text/plain"}
       :body "User not found"})))

(defn edit-user [request]
  (let [id (get-in request [:query-params "id"])]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (user-pages/edit (auth-service/get-user-by-id id))}))

(defn update-user [request]
  (let [id (get-in request [:form-params "id"])
        {:strs [username email]} (:form-params request)]
    (auth-service/update-user! id {:username username :email email})
    {:status 302
     :headers {"Location" "/private/auth/list-users"}
     :body ""}))

(defn delete-user [request]
  (let [id (get-in request [:form-params "id"])]
    (auth-service/delete-user! id)
    {:status 302
     :headers {"Location" "/private/auth/list-users"}
     :body ""}))


;; -------------------
;; Role handlers
;; -------------------

(defn list-roles [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (role-pages/index (auth-service/list-roles) nil)})

(defn new-role [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (role-pages/new)})

(defn create-role [request]
  (let [{:strs [name description]} (:form-params request)]
    (auth-service/create-role! {:name name :description description})
    {:status 302
     :headers {"Location" "/private/auth/list-roles"}
     :body ""}))

(defn show-role [{:keys [query-params]}]
  (let [id   (query-params "id")
        role (auth-service/get-role-by-id id)]
    (if role
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (role-pages/show role)}
      {:status  404
       :headers {"Content-Type" "text/plain"}
       :body    "Role not found"})))

(defn edit-role [request]
  (let [id (get-in request [:query-params "id"])]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (role-pages/edit (auth-service/get-role-by-id id))}))

(defn update-role [request]
  (let [id (get-in request [:form-params "id"])
        {:strs [name description]} (:form-params request)]
    (auth-service/update-role! id {:name name :description description})
    {:status 302
     :headers {"Location" "/private/auth/list-roles"}
     :body ""}))

(defn delete-role [request]
  (let [id (get-in request [:form-params "id"])]
    (auth-service/delete-role! id)
    {:status 302
     :headers {"Location" "/private/auth/list-roles"}
     :body ""}))

;; -------------------
;; Permission handlers
;; -------------------

(defn list-permissions [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (permission-pages/index (auth-service/list-permissions) nil)})

(defn new-permission [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (permission-pages/new)})

(defn create-permission [request]
  (let [{:strs [name description]} (:form-params request)]
    (auth-service/create-permission! {:name name :description description})
    {:status 302
     :headers {"Location" "/private/auth/list-permissions"}
     :body ""}))

(defn show-permission [{:keys [query-params]}]
  (let [id   (query-params "id")
        permission (auth-service/get-permission-by-id id)]
    (if permission
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (permission-pages/show permission)}
      {:status  404
       :headers {"Content-Type" "text/plain"}
       :body    "Permission not found"})))

(defn edit-permission [request]
  (let [id (get-in request [:query-params "id"])]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (permission-pages/edit (auth-service/get-permission-by-id id))}))

(defn update-permission [request]
  (let [id (get-in request [:form-params "id"])
        {:strs [name description]} (:form-params request)]
    (auth-service/update-permission! id {:name name :description description})
    {:status 302
     :headers {"Location" "/private/auth/list-permissions"}
     :body ""}))

(defn delete-permission [request]
  (let [id (get-in request [:form-params "id"])]
    (auth-service/delete-permission! id)
    {:status 302
     :headers {"Location" "/private/auth/list-permissions"}
     :body ""}))

;; -------------------
;; Resource handlers
;; -------------------

(defn list-resources [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (resource-pages/index
             (auth-service/list-resources)
             nil)})

(defn new-resource [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (resource-pages/new)})

(defn create-resource [{:keys [form-params]}]
  (let [{:strs [slug name description]} form-params]
    (auth-service/create-resource! {:slug        slug
                                    :name        name
                                    :description description})
    {:status  302
     :headers {"Location" "/private/auth/list-resources"}
     :body    ""}))

(defn show-resource [{:keys [query-params]}]
  (let [id   (query-params "id")
        resource (auth-service/get-resource-by-id id)]
    (if resource
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (resource-pages/show resource)}
      {:status  404
       :headers {"Content-Type" "text/plain"}
       :body    "Resource not found"})))

(defn edit-resource [{:keys [query-params]}]
  (let [id   (query-params "id")
        resource (auth-service/get-resource-by-id id)]
    (if resource
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (resource-pages/edit resource)}
      {:status  404
       :headers {"Content-Type" "text/plain"}
       :body    "Resource not found"})))

(defn update-resource [{:keys [form-params]}]
  (let [id                  (form-params "id")
        {:strs [slug name description]} form-params]
    (auth-service/update-resource! id {:slug        slug
                                       :name        name
                                       :description description})
    {:status  302
     :headers {"Location" "/private/auth/list-resources"}
     :body    ""}))

(defn delete-resource [{:keys [form-params]}]
  (when-let [id (form-params "id")]
    (auth-service/delete-resource! id))
  {:status  302
   :headers {"Location" "/private/auth/list-resources"}
   :body    ""})

;; -------------------
;; Relationships handlers
;; -------------------

(defn list-user-roles [request]
  (let [user-id (get-in request [:query-params "id"])
        roles (auth-service/get-roles-with-assignment-status-ui user-id)]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (hiccup.page/html5 (role-pages/list-user-roles user-id roles))}))

(defn assign-role [request]
  (let [user-id (get-in request [:form-params "user-id"])
        role-id (get-in request [:form-params "role-id"])]
    (log/debug "assign_role_handler user_id:" user-id "role_id:" role-id)
    (when (and user-id role-id)
      (try
        (let [result (auth-service/assign-role-to-user! user-id role-id)]
          (log/debug "assign_role_handler assign_role_to_user_result:" result))
        (catch Exception e
          (log/debug "assign_role_handler exception:" (.getMessage e)))))
    {:status 302
     :headers {"Location" (str "/private/auth/list-user-roles?id=" user-id)}
     :body ""}))

(defn unassign-role [request]
  (let [{:strs [user-id role-id]} (:form-params request)]
    (auth-service/unassign-role-from-user! user-id role-id)
    {:status 302
     :headers {"Location" (str "/private/auth/list-user-roles?id=" user-id)}
     :body ""}))

(defn list-user-permissions [request]
  (let [user-id (get-in request [:query-params "id"])
        permissions (auth-service/get-permissions-with-assignment-status user-id)]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (hiccup.page/html5 (permission-pages/list-user-permissions user-id permissions))}))

(defn assign-permission [request]
  (let [{:strs [user-id permission-id]} (:form-params request)]
    (auth-service/assign-permission-to-user! user-id permission-id)
    {:status 302
     :headers {"Location" (str "/private/auth/list-user-permissions?id=" user-id)}
     :body ""}))

(defn unassign-permission [request]
  (let [{:strs [user-id permission-id]} (:form-params request)]
    (auth-service/unassign-permission-from-user! user-id permission-id)
    {:status 302
     :headers {"Location" (str "/private/auth/list-user-permissions?id=" user-id)}
     :body ""}))

(defn list-role-permissions [request]
  (let [role-id (get-in request [:query-params "id"])
        permissions (auth-service/get-permissions-with-assignment-status-for-role role-id)]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (hiccup.page/html5 (permission-pages/list-role-permissions role-id permissions))}))

(defn assign-permission-to-role [request]
  (let [{:strs [role-id permission-id]} (:form-params request)]
    (auth-service/assign-permission-to-role! role-id permission-id)
    {:status 302
     :headers {"Location" (str "/private/auth/list-role-permissions?id=" role-id)}
     :body ""}))

(defn unassign-permission-from-role [request]
  (let [{:strs [role-id permission-id]} (:form-params request)]
    (auth-service/unassign-permission-from-role! role-id permission-id)
    {:status 302
     :headers {"Location" (str "/private/auth/list-role-permissions?id=" role-id)}
     :body ""}))

(defn list-resource-permissions [request]
  (let [resource-id (get-in request [:query-params "id"])
        permissions (auth-service/get-permissions-with-assignment-status-for-resource resource-id)]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (hiccup.page/html5 (permission-pages/list-resource-permissions resource-id permissions))}))

(defn assign-permission-to-resource [request]
  (let [{:strs [resource-id permission-id]} (:form-params request)]
    (auth-service/assign-permission-to-resource! resource-id permission-id)
    {:status 302
     :headers {"Location" (str "/private/auth/list-resource-permissions?id=" resource-id)}
     :body ""}))

(defn unassign-permission-from-resource [request]
  (let [{:strs [resource-id permission-id]} (:form-params request)]
    (auth-service/unassign-permission-from-resource! resource-id permission-id)
    {:status 302
     :headers {"Location" (str "/private/auth/list-resource-permissions?id=" resource-id)}
     :body ""}))
