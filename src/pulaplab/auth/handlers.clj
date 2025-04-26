(ns pulaplab.auth.handlers
  (:require [pulaplab.auth.ui.pages :as pages]
            [pulaplab.auth.db :as auth-db]))

(defn list-users-handler [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (pages/list-users-page (auth-db/list-users) nil)})

(defn new-user-handler [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (pages/new-user-page)})

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
       :body (pages/show-user-page user)}
      {:status 404
       :headers {"Content-Type" "text/plain"}
       :body "User not found"})))

(defn edit-user-handler [request]
  (let [id (get-in request [:query-params "id"])
        user (auth-db/get-user-by-id id)]
    (if user
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (pages/edit-user-page user)}
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
