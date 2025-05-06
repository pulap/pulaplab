(ns pulaplab.auth.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [pulaplab.db.core :as db :refer [db-spec]]
            [pulaplab.auth.query :refer [queries]])
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
  (->> (jdbc/execute! db/datasource
                      [(queries :list-users)]
                      {:builder-fn rs/as-unqualified-lower-maps})
       (map (fn [row]
              {:id (:id row)
               :username (:username row)
               :email (some-> (:email_enc row) (String.))}))))

(defn get-user-by-id [id]
  (let [row (first (jdbc/execute! db/datasource
                                  [(queries :get-user-by-id) id]
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
  (->> (jdbc/execute! db/datasource
                      [(queries :list-roles)]
                      {:builder-fn rs/as-unqualified-lower-maps})
       (map (fn [row]
              {:id          (:id row)
               :slug        (:slug row)
               :name        (:name row)
               :description (:description row)}))))

(defn get-role-by-id [id]
  (when-let [row (first (jdbc/execute! db/datasource
                                       [(queries :get-role-by-id) id]
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
  (->> (jdbc/execute! db/datasource
                      [(queries :list-permissions)]
                      {:builder-fn rs/as-unqualified-lower-maps})
       (map (fn [row]
              {:id          (:id row)
               :slug        (:slug row)
               :name        (:name row)
               :description (:description row)}))))

(defn get-permission-by-id [id]
  (when-let [row (first (jdbc/execute! db/datasource
                                       [(queries :get-permission-by-id) id]
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
  (->> (jdbc/execute! db/datasource
                      [(queries :list-resources)]
                      {:builder-fn rs/as-unqualified-lower-maps})
       (map (fn [row]
              {:id          (:id row)
               :slug        (:slug row)
               :name        (:name row)
               :description (:description row)}))))

(defn get-resource-by-id [id]
  (when-let [row (first (jdbc/execute! db/datasource
                                       [(queries :get-resource-by-id) id]
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

(defn assign-role-to-user! [user-id role-id]
  (jdbc/execute! db/datasource
                 [(queries :assign-role-to-user) user-id role-id]))

(defn unassign-role-from-user!
  [user-id role-id]
  (jdbc/execute! db/datasource
                 [(queries :unassign-role-from-user) user-id role-id]))

(defn get-user-permissions [user-id]
  (jdbc/execute! db/datasource
                 [(queries :get-user-permissions) user-id user-id]
                 {:builder-fn rs/as-unqualified-lower-maps}))

(defn get-permissions-with-assignment-status [user-id]
  (jdbc/execute! db/datasource
                 [(queries :get-permissions-with-assignment-status) user-id user-id]
                 {:builder-fn rs/as-unqualified-lower-maps}))

(defn assign-permission-to-user! [user-id permission-id]
  (jdbc/execute! db/datasource
                 [(queries :assign-permission-to-user) user-id permission-id]))

(defn unassign-permission-from-user! [user-id permission-id]
  (jdbc/execute! db/datasource
                 [(queries :unassign-permission-from-user) user-id permission-id]))

;; ---------------------------
;; Role-Permission Relationships
;; ---------------------------

(defn get-permissions-with-assignment-status-for-role [role-id]
  (println "Debug: Executing get-permissions-with-assignment-status-for-role query for role-id" role-id)
  (let [result (jdbc/execute! db/datasource [(queries :get-permissions-with-assignment-status-for-role) role-id role-id] {:builder-fn rs/as-unqualified-lower-maps})]
    (println "Debug: Query result" result)
    result))

(defn assign-permission-to-role! [role-id permission-id]
  (println "Debug: Assigning permission" permission-id "to role" role-id)
  (jdbc/execute! db/datasource [(queries :assign-permission-to-role) role-id permission-id]))

(defn unassign-permission-from-role! [role-id permission-id]
  (println "Debug: Unassigning permission" permission-id "from role" role-id)
  (jdbc/execute! db/datasource [(queries :unassign-permission-from-role) role-id permission-id]))

;; ---------------------------
;; Resource-Permission Relationships
;; ---------------------------

(defn get-permissions-with-assignment-status-for-resource [resource-id]
  (println "Debug: Executing get-permissions-with-assignment-status-for-resource query for resource-id" resource-id)
  (let [result (jdbc/execute! db/datasource [(queries :get-permissions-with-assignment-status-for-resource) resource-id resource-id] {:builder-fn rs/as-unqualified-lower-maps})]
    (println "Debug: Query result" result)
    result))

(defn assign-permission-to-resource! [resource-id permission-id]
  (jdbc/execute! db/datasource [(queries :assign-permission-to-resource) resource-id permission-id]))

(defn unassign-permission-from-resource! [resource-id permission-id]
  (jdbc/execute! db/datasource [(queries :unassign-permission-from-resource) resource-id permission-id]))

(defn get-resources-with-permission [permission-id]
  (println "Debug: Fetching resources with permission" permission-id)
  (jdbc/execute! db/datasource [(queries :get-resources-with-permission) permission-id permission-id] {:builder-fn rs/as-unqualified-lower-maps}))