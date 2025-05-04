(ns pulaplab.auth.handlers
  (:require [pulaplab.auth.ui.user :as user-pages]
            [pulaplab.auth.ui.role :as role-pages]
            [pulaplab.auth.ui.permission :as permission-pages]
            [pulaplab.auth.ui.resource :as resource-pages]
            [pulaplab.auth.db :as auth-db]))

;; -------------------
;; User handlers
;; -------------------

(defn list-users-handler [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (user-pages/index (auth-db/list-users) nil)})

(defn new-user-handler [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (user-pages/new)})

(defn create-user-handler [request]
  (let [{:strs [username email]} (:form-params request)]
    (auth-db/create-user! {:username username :email email})
    {:status 302
     :headers {"Location" "/private/auth/list-users"}
     :body ""}))

(defn show-user-handler [request]
  (let [id (get-in request [:query-params "id"])
        user (auth-db/get-user-by-id id)]
    (if user
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (user-pages/show user)}
      {:status 404
       :headers {"Content-Type" "text/plain"}
       :body "User not found"})))

(defn edit-user-handler [request]
  (let [id (get-in request [:query-params "id"])
        user (auth-db/get-user-by-id id)]
    (if user
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (user-pages/edit user)}
      {:status 404
       :headers {"Content-Type" "text/plain"}
       :body "User not found"})))

(defn update-user-handler [request]
  (let [id (get-in request [:query-params "id"])
        {:strs [username email]} (:form-params request)]
    (auth-db/update-user! id {:username username :email email})
    {:status 302
     :headers {"Location" "/private/auth/list-users"}
     :body ""}))

(defn delete-user-handler [request]
  (let [id (get-in request [:form-params "id"])]
    (when id
      (auth-db/delete-user! id))
    {:status 302
     :headers {"Location" "/private/auth/list-users"}
     :body ""}))


;; -------------------
;; Role handlers
;; -------------------

(defn list-roles-handler
  [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (role-pages/index
             (auth-db/list-roles)
             nil)})

(defn new-role-handler
  [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (role-pages/new)})

(defn create-role-handler
  [{:keys [form-params]}]
  (let [{:strs [slug name description]} form-params]
    (auth-db/create-role! {:slug        slug
                           :name        name
                           :description description})
    {:status  302
     :headers {"Location" "/private/auth/list-roles"}
     :body    ""}))

(defn show-role-handler
  [{:keys [query-params]}]
  (let [id   (query-params "id")
        role (auth-db/get-role-by-id id)]
    (if role
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (role-pages/show role)}
      {:status  404
       :headers {"Content-Type" "text/plain"}
       :body    "Role not found"})))

(defn edit-role-handler
  [{:keys [query-params]}]
  (let [id   (query-params "id")
        role (auth-db/get-role-by-id id)]
    (if role
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (role-pages/edit role)}
      {:status  404
       :headers {"Content-Type" "text/plain"}
       :body    "Role not found"})))

(defn update-role-handler
  [{:keys [query-params form-params]}]
  (let [id                  (query-params "id")
        {:strs [slug name description]} form-params]
    (auth-db/update-role! id {:slug        slug
                              :name        name
                              :description description})
    {:status  302
     :headers {"Location" "/private/auth/list-roles"}
     :body    ""}))

(defn delete-role-handler
  [{:keys [form-params]}]
  (when-let [id (form-params "id")]
    (auth-db/delete-role! id))
  {:status  302
   :headers {"Location" "/private/auth/list-roles"}
   :body    ""})

;; -------------------
;; Permission handlers
;; -------------------

(defn list-permissions-handler
  [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (permission-pages/index
             (auth-db/list-permissions)
             nil)})

(defn new-permission-handler
  [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (permission-pages/new)})

(defn create-permission-handler
  [{:keys [form-params]}]
  (let [{:strs [slug name description]} form-params]
    (auth-db/create-permission! {:slug        slug
                           :name        name
                           :description description})
    {:status  302
     :headers {"Location" "/private/auth/list-permissions"}
     :body    ""}))

(defn show-permission-handler
  [{:keys [query-params]}]
  (let [id   (query-params "id")
        permission (auth-db/get-permission-by-id id)]
    (if permission
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (permission-pages/show permission)}
      {:status  404
       :headers {"Content-Type" "text/plain"}
       :body    "Permission not found"})))

(defn edit-permission-handler
  [{:keys [query-params]}]
  (let [id   (query-params "id")
        permission (auth-db/get-permission-by-id id)]
    (if permission
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (permission-pages/edit permission)}
      {:status  404
       :headers {"Content-Type" "text/plain"}
       :body    "Permission not found"})))

(defn update-permission-handler
  [{:keys [query-params form-params]}]
  (let [id                  (query-params "id")
        {:strs [slug name description]} form-params]
    (auth-db/update-permission! id {:slug        slug
                              :name        name
                              :description description})
    {:status  302
     :headers {"Location" "/private/auth/list-permissions"}
     :body    ""}))

(defn delete-permission-handler
  [{:keys [form-params]}]
  (when-let [id (form-params "id")]
    (auth-db/delete-permission! id))
  {:status  302
   :headers {"Location" "/private/auth/list-permissions"}
   :body    ""})

;; -------------------
;; Resource handlers
;; -------------------

(defn list-resources-handler
  [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (resource-pages/index
             (auth-db/list-resources)
             nil)})

(defn new-resource-handler
  [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (resource-pages/new)})

(defn create-resource-handler
  [{:keys [form-params]}]
  (let [{:strs [slug name description]} form-params]
    (auth-db/create-resource! {:slug        slug
                           :name        name
                           :description description})
    {:status  302
     :headers {"Location" "/private/auth/list-resources"}
     :body    ""}))

(defn show-resource-handler
  [{:keys [query-params]}]
  (let [id   (query-params "id")
        resource (auth-db/get-resource-by-id id)]
    (if resource
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (resource-pages/show resource)}
      {:status  404
       :headers {"Content-Type" "text/plain"}
       :body    "Resource not found"})))

(defn edit-resource-handler
  [{:keys [query-params]}]
  (let [id   (query-params "id")
        resource (auth-db/get-resource-by-id id)]
    (if resource
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (resource-pages/edit resource)}
      {:status  404
       :headers {"Content-Type" "text/plain"}
       :body    "Resource not found"})))

(defn update-resource-handler
  [{:keys [query-params form-params]}]
  (let [id                  (query-params "id")
        {:strs [slug name description]} form-params]
    (auth-db/update-resource! id {:slug        slug
                              :name        name
                              :description description})
    {:status  302
     :headers {"Location" "/private/auth/list-resources"}
     :body    ""}))

(defn delete-resource-handler
  [{:keys [form-params]}]
  (when-let [id (form-params "id")]
    (auth-db/delete-resource! id))
  {:status  302
   :headers {"Location" "/private/auth/list-resources"}
   :body    ""})
