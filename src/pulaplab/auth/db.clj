(ns pulaplab.auth.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [pulaplab.db.core :as db]
            [pulaplab.auth.query :refer [queries get-query]]
            [clojure.tools.logging :as log])
  (:import (java.util UUID)))

;; ---------------------------
;; Helpers
;; ---------------------------

(defn generate-uuid []
  (str (UUID/randomUUID)))

;; ---------------------------
;; Users
;; ---------------------------

;; Emails are stored in the database as BLOBs (email_enc).
;; No real encryption is applied yet: we simply store UTF-8 bytes.
;; This design allows for real encryption later without breaking compatibility.
;; When reading, we manually convert BLOBs back to UTF-8 strings.
;; We also normalize the keys (e.g., :email instead of :email_enc) to simplify views.

(defn list-users []
  (log/debug "DB list-users query")
  (let [rows (jdbc/execute! db/datasource [(get-query :list-users)] {:builder-fn rs/as-unqualified-lower-maps})
        users (map (fn [row] {:id (:id row)
                              :username (:username row)
                              :email (some-> (:email_enc row) (String.))})
                   rows)]
    (log/debug "DB list-users result:" users)
    users))

(defn get-user-by-id [id]
  (let [row (first (jdbc/execute! db/datasource
                                  [(get-query :get-user-by-id) id]
                                  {:builder-fn rs/as-unqualified-lower-maps}))]
    (when row
      {:id (:id row)
       :username (:username row)
       :email (some-> (:email_enc row) (String.))})))

(defn get-user-by-username [username]
  (let [row (first (jdbc/execute! db/datasource
                                  [(get-query :get-user-by-username) username]
                                  {:builder-fn rs/as-unqualified-lower-maps}))]
    (when row
      {:id (:id row)
       :username (:username row)
       :email (some-> (:email_enc row) (String.))})))

(defn create-user! [{:keys [username email]}]
  (jdbc/execute! db/datasource
                 [(queries :create-user)
                  (generate-uuid)
                  username
                  (.getBytes email "UTF-8")]))

(defn update-user! [id {:keys [username email]}]
  (jdbc/execute! db/datasource
                 [(queries :update-user)
                  username
                  (.getBytes email "UTF-8")
                  id]))

(defn delete-user! [id]
  (jdbc/execute! db/datasource
                 [(queries :delete-user) id]))

;; ---------------------------
;; Roles
;; ---------------------------

(defn list-roles []
  (log/debug "DB list-roles query")
  (let [rows (jdbc/execute! db/datasource [(get-query :list-roles)] {:builder-fn rs/as-unqualified-lower-maps})
        roles (map (fn [row] {:id (:id row)
                              :slug (:slug row)
                              :name (:name row)
                              :description (:description row)})
                   rows)]
    (log/debug "DB list-roles result:" roles)
    roles))

(defn get-role-by-id [id]
  (when-let [row (first (jdbc/execute! db/datasource
                                       [(get-query :get-role-by-id) id]
                                       {:builder-fn rs/as-unqualified-lower-maps}))]
    {:id          (:id row)
     :slug        (:slug row)
     :name        (:name row)
     :description (:description row)}))

(defn create-role! [{:keys [slug name description]}]
  (jdbc/execute! db/datasource
                 [(queries :create-role)
                  (generate-uuid)
                  slug
                  name
                  description]))

(defn update-role! [id {:keys [slug name description]}]
  (jdbc/execute! db/datasource
                 [(queries :update-role)
                  slug
                  name
                  description
                  id]))

(defn delete-role! [id]
  (jdbc/execute! db/datasource
                 [(queries :delete-role) id]))

;; ---------------------------
;; Permissions
;; ---------------------------

(defn list-permissions []
  (log/debug "DB list-permissions query")
  (let [rows (jdbc/execute! db/datasource [(get-query :list-permissions)] {:builder-fn rs/as-unqualified-lower-maps})
        perms (map (fn [row] {:id (:id row)
                              :slug (:slug row)
                              :name (:name row)
                              :description (:description row)})
                   rows)]
    (log/debug "DB list-permissions result:" perms)
    perms))

(defn get-permission-by-id [id]
  (when-let [row (first (jdbc/execute! db/datasource
                                       [(get-query :get-permission-by-id) id]
                                       {:builder-fn rs/as-unqualified-lower-maps}))]
    {:id          (:id row)
     :slug        (:slug row)
     :name        (:name row)
     :description (:description row)}))

(defn create-permission! [{:keys [slug name description]}]
  (jdbc/execute! db/datasource
                 [(queries :create-permission)
                  (generate-uuid)
                  slug
                  name
                  description]))

(defn update-permission! [id {:keys [slug name description]}]
  (jdbc/execute! db/datasource
                 [(queries :update-permission)
                  slug
                  name
                  description
                  id]))

(defn delete-permission! [id]
  (jdbc/execute! db/datasource
                 [(queries :delete-permission) id]))

;; ---------------------------
;; Resources
;; ---------------------------

(defn list-resources []
  (log/debug "DB list-resources query")
  (let [rows (jdbc/execute! db/datasource [(get-query :list-resources)] {:builder-fn rs/as-unqualified-lower-maps})
        resources (map (fn [row] {:id (:id row)
                                  :slug (:slug row)
                                  :name (:name row)
                                  :description (:description row)})
                       rows)]
    (log/debug "DB list-resources result:" resources)
    resources))

(defn get-resource-by-id [id]
  (when-let [row (first (jdbc/execute! db/datasource
                                       [(get-query :get-resource-by-id) id]
                                       {:builder-fn rs/as-unqualified-lower-maps}))]
    {:id          (:id row)
     :slug        (:slug row)
     :name        (:name row)
     :description (:description row)}))

(defn create-resource! [{:keys [slug name description]}]
  (jdbc/execute! db/datasource
                 [(queries :create-resource)
                  (generate-uuid)
                  slug
                  name
                  description]))

(defn update-resource! [id {:keys [slug name description]}]
  (jdbc/execute! db/datasource
                 [(queries :update-resource)
                  slug
                  name
                  description
                  id]))

(defn delete-resource! [id]
  (jdbc/execute! db/datasource
                 [(queries :delete-resource) id]))


;; ---------------------------
;; Relationships
;; ---------------------------

(defn get-roles-with-assignment-status [user-id]
  (jdbc/execute! db/datasource
                 [(queries :get-roles-with-assignment-status) user-id]
                 {:builder-fn rs/as-unqualified-lower-maps}))

;; Retrieves role IDs assigned to a user
(defn get-user-roles [user-id]
  (->> (jdbc/execute! db/datasource
                      [(queries :get-user-roles) user-id]
                      {:builder-fn rs/as-unqualified-lower-maps})
       (map :role_id)))

(defn assign-role-to-user! [user-id role-id]
  (log/debug "[db] assign-role-to-user! user-id:" user-id "role-id:" role-id)
  (try
    (let [result (jdbc/execute! db/datasource
                                [(queries :assign-role-to-user) user-id role-id])]
      (log/debug "[db] assign-role-to-user! jdbc result:" result)
      result)
    (catch Exception e
      (log/debug "[db] assign-role-to-user! Exception:" (.getMessage e)))))

(defn unassign-role-from-user!
  [user-id role-id]
  (jdbc/execute! db/datasource
                 [(queries :unassign-role-from-user) user-id role-id]))

(defn get-user-permissions [user-id]
  (->> (jdbc/execute! db/datasource
                      [(queries :get-user-permissions) user-id user-id]
                      {:builder-fn rs/as-unqualified-lower-maps})
       (map :permission_id)))

(defn get-permissions-with-assignment-status-for-user [user-id]
  (jdbc/execute! db/datasource
                 [(queries :get-permissions-with-assignment-status-for-user) user-id user-id]
                 {:builder-fn rs/as-unqualified-lower-maps}))

(defn assign-permission-to-user! [user-id permission-id]
  (let [sql (get-query :assign-permission-to-user)]
    (log/debug "[db] assign-permission-to-user! SQL:" sql "user-id:" user-id "permission-id:" permission-id)
    (let [result (jdbc/execute! db/datasource [sql user-id permission-id])]
      (log/debug "[db] assign-permission-to-user! result:" result)
      result)))

(defn unassign-permission-from-user! [user-id permission-id]
  (jdbc/execute! db/datasource
                 [(queries :unassign-permission-from-user) user-id permission-id]))

;; ---------------------------
;; Role-Permission Relationships
;; ---------------------------

;; Retrieves permission IDs assigned to a role
(defn get-role-permissions [role-id]
  (->> (jdbc/execute! db/datasource
                      [(queries :get-role-permissions) role-id]
                      {:builder-fn rs/as-unqualified-lower-maps})
       (map :permission_id)))

(defn get-permissions-with-assignment-status-for-role [role-id]
  (log/debug "Executing get-permissions-with-assignment-status-for-role query for role-id" role-id)
  (let [result (jdbc/execute! db/datasource [(queries :get-permissions-with-assignment-status-for-role) role-id role-id] {:builder-fn rs/as-unqualified-lower-maps})]
    (log/debug "Query result" result)
    result))

(defn assign-permission-to-role! [role-id permission-id]
  (log/debug "Assigning permission" permission-id "to role" role-id)
  (jdbc/execute! db/datasource [(queries :assign-permission-to-role) role-id permission-id]))

(defn unassign-permission-from-role! [role-id permission-id]
  (log/debug "Unassigning permission" permission-id "from role" role-id)
  (jdbc/execute! db/datasource [(queries :unassign-permission-from-role) role-id permission-id]))

;; ---------------------------
;; Resource-Permission Relationships
;; ---------------------------

;; Retrieves permission IDs assigned to a resource
(defn get-resource-permissions [resource-id]
  (->> (jdbc/execute! db/datasource
                      [(queries :get-resource-permissions) resource-id]
                      {:builder-fn rs/as-unqualified-lower-maps})
       (map :permission_id)))

(defn get-permissions-with-assignment-status-for-resource [resource-id]
  (log/debug "Executing get-permissions-with-assignment-status-for-resource query for resource-id" resource-id)
  (let [result (jdbc/execute! db/datasource [(queries :get-permissions-with-assignment-status-for-resource) resource-id resource-id] {:builder-fn rs/as-unqualified-lower-maps})]
    (log/debug "Query result" result)
    result))

(defn assign-permission-to-resource! [resource-id permission-id]
  (jdbc/execute! db/datasource [(queries :assign-permission-to-resource) resource-id permission-id]))

(defn unassign-permission-from-resource! [resource-id permission-id]
  (jdbc/execute! db/datasource [(queries :unassign-permission-from-resource) resource-id permission-id]))

(defn get-resources-with-permission [permission-id]
  (log/debug "Fetching resources with permission" permission-id)
  (jdbc/execute! db/datasource [(queries :get-resources-with-permission) permission-id permission-id] {:builder-fn rs/as-unqualified-lower-maps}))