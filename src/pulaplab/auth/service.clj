(ns pulaplab.auth.service
  (:require [clojure.set :as set]
            [pulaplab.auth.db :as auth-db]
            [clojure.tools.logging :as log]))

;; WIP: Ongoing refactor moving logic from handlers into service layer; 
;; navigation and form errors introduced here will be addressed in this PR.

;; --------------------------
;; User functions
;; --------------------------

(defn list-users []
  (auth-db/list-users))

(defn get-user-by-id [id]
  (auth-db/get-user-by-id id))

(defn create-user! [user-data]
  ;; TODO: Implement validations.
  (auth-db/create-user! user-data))

(defn update-user! [id user-data]
  ;; TODO: Implement validations.
  (auth-db/update-user! id user-data))

(defn delete-user! [id]
  (auth-db/delete-user! id))

;; --------------------------
;; Role functions
;; --------------------------

(defn list-roles []
  (auth-db/list-roles))

(defn get-role-by-id [id]
  (auth-db/get-role-by-id id))

(defn create-role! [role-data]
  ;; TODO: Implement validations.
  (auth-db/create-role! role-data))

(defn update-role! [id role-data]
  ;; TODO: Implement validations.
  (auth-db/update-role! id role-data))

(defn delete-role! [id]
  (auth-db/delete-role! id))

;; --------------------------
;; Permission functions
;; --------------------------

(defn list-permissions []
  (auth-db/list-permissions))

(defn get-permission-by-id [id]
  (auth-db/get-permission-by-id id))

(defn create-permission! [permission-data]
  ;; TODO: Implement validations.
  (auth-db/create-permission! permission-data))

(defn update-permission! [id permission-data]
  ;; TODO: Implement validations.
  (auth-db/update-permission! id permission-data))

(defn delete-permission! [id]
  (auth-db/delete-permission! id))

;; --------------------------
;; Resource functions
;; --------------------------

(defn list-resources []
  (auth-db/list-resources))

(defn get-resource-by-id [id]
  (auth-db/get-resource-by-id id))

(defn create-resource! [resource-data]
  ;; TODO: Implement validations.
  (auth-db/create-resource! resource-data))

(defn update-resource! [id resource-data]
  ;; TODO: Implement validations.
  (auth-db/update-resource! id resource-data))

(defn delete-resource! [id]
  (auth-db/delete-resource! id))

;; --------------------------
;; User-Role relationship
;; --------------------------

(defn assign-role-to-user! [user-id role-id]
  (log/debug "Service assign-role-to-user user-id:" user-id "role-id:" role-id)
  (try
    (let [result (auth-db/assign-role-to-user! user-id role-id)]
      (log/debug "Service assign-role-to-user DB result:" result)
      result)
    (catch Exception e
      (log/debug "Service assign-role-to-user Exception:" (.getMessage e)))))

(defn unassign-role-from-user! [user-id role-id]
  (auth-db/unassign-role-from-user! user-id role-id))

(defn get-roles-with-assignment-status [user-id]
  (auth-db/get-roles-with-assignment-status user-id))

(defn get-roles-with-assignment-status-ui [user-id]
  (let [roles (auth-db/get-roles-with-assignment-status user-id)]
    (map (fn [role]
           {:role_id (:id role)
            :role_name (:name role)
            :role_description (:description role)
            :is_assigned (if (= "Assigned" (:status role)) 1 0)})
         roles)))

(defn get-user-roles [user-id]
  (auth-db/get-user-roles user-id))

;; --------------------------
;; User-Permission relationship
;; --------------------------
(defn get-permissions-with-assignment-status [user-id]
  (auth-db/get-permissions-with-assignment-status-for-user user-id))

(defn assign-permission-to-user! [user-id permission-id]
  (auth-db/assign-permission-to-user! user-id permission-id))

(defn unassign-permission-from-user! [user-id permission-id]
  (auth-db/unassign-permission-from-user! user-id permission-id))

(defn get-user-permissions [user-id]
  (auth-db/get-user-permissions user-id))

;; --------------------------
;; Role-Permission relationship
;; --------------------------

(defn assign-permission-to-role! [role-id permission-id]
  (auth-db/assign-permission-to-role! role-id permission-id))

(defn unassign-permission-from-role! [role-id permission-id]
  (auth-db/unassign-permission-from-role! role-id permission-id))

(defn get-role-permissions [role-id]
  (auth-db/get-role-permissions role-id))

;; Delegates retrieval of permission assignment status for a role
(defn get-permissions-with-assignment-status-for-role [role-id]
  (auth-db/get-permissions-with-assignment-status-for-role role-id))

;; --------------------------
;; Resource-Permission relationship
;; --------------------------

(defn assign-permission-to-resource! [resource-id permission-id]
  (auth-db/assign-permission-to-resource! resource-id permission-id))

(defn unassign-permission-from-resource! [resource-id permission-id]
  (auth-db/unassign-permission-from-resource! resource-id permission-id))

(defn get-resource-permissions [resource-id]
  (auth-db/get-resource-permissions resource-id))

;; Delegates retrieval of permission assignment status for a resource
(defn get-permissions-with-assignment-status-for-resource [resource-id]
  (auth-db/get-permissions-with-assignment-status-for-resource resource-id))

;; Delegates retrieval of resources assignment for a permission
(defn get-resources-with-permission [permission-id]
  (auth-db/get-resources-with-permission permission-id))

;; --------------------------
;; Authorization logic
;; --------------------------

(defn get-user-effective-permissions [user-id]
  (let [direct-perms (get-user-permissions user-id)
        roles (get-user-roles user-id)
        perms-from-roles (mapcat #(get-role-permissions %) roles)]
    (set (concat direct-perms perms-from-roles))))

(defn can-access-resource? [user-id resource-id]
  (let [user-perms (get-user-effective-permissions user-id)
        resource-perms (get-resource-permissions resource-id)]
    (boolean (seq (clojure.set/intersection (set user-perms) (set resource-perms))))))

;; --------------------------
;; Authentication functions
;; --------------------------

(defn authenticate-user [username _password]
  ;; TODO: Implement user authentication with proper password hashing
  (when-let [user (auth-db/get-user-by-username username)]
    ;; For now, this is a simple placeholder - implement proper authentication!
    ;; This should be replaced with secure password verification
    (assoc user :authenticated true)))

(defn create-session [user]
  ;; TODO: Implement session creation logic
  ;; Generate a session token and store it with user info
  {:token (str (java.util.UUID/randomUUID))
   :user-id (:id user)})
