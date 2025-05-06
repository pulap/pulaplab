(ns pulaplab.auth.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [pulaplab.db.core :as db :refer [db-spec]])
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
                      ["SELECT id, username, email_enc FROM users ORDER BY username"]
                      {:builder-fn rs/as-unqualified-lower-maps})
       (map (fn [row]
              {:id (:id row)
               :username (:username row)
               :email (some-> (:email_enc row) (String.))}))))

(defn get-user-by-id [id]
  (let [row (first (jdbc/execute! db/datasource
                                  ["SELECT id, username, email_enc FROM users WHERE id = ?" id]
                                  {:builder-fn rs/as-unqualified-lower-maps}))]
    (when row
      {:id (:id row)
       :username (:username row)
       :email (some-> (:email_enc row) (String.))})))


(defn create-user! [{:keys [username email]}]
  (jdbc/execute! db/datasource
                 ["INSERT INTO users (id, username, email_enc, created_at, updated_at, is_active)
                   VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)"
                  (generate-uuid)
                  username
                  (.getBytes email "UTF-8")]))

(defn update-user! [id {:keys [username email]}]
  (jdbc/execute! db/datasource
                 ["UPDATE users
                   SET username = ?, email_enc = ?, updated_at = CURRENT_TIMESTAMP
                   WHERE id = ?"
                  username
                  (.getBytes email "UTF-8")
                  id]))

(defn delete-user! [id]
  (jdbc/execute! db/datasource
                 ["DELETE FROM users WHERE id = ?" id]))

;; ---------------------------
;; Roles
;; ---------------------------

(defn list-roles []
  (->> (jdbc/execute! db/datasource
                      ["SELECT id, slug, name, description FROM roles ORDER BY name"]
                      {:builder-fn rs/as-unqualified-lower-maps})
       (map (fn [row]
              {:id          (:id row)
               :slug        (:slug row)
               :name        (:name row)
               :description (:description row)}))))

(defn get-role-by-id [id]
  (when-let [row (first (jdbc/execute! db/datasource
                                       ["SELECT id, slug, name, description FROM roles WHERE id = ?" id]
                                       {:builder-fn rs/as-unqualified-lower-maps}))]
    {:id          (:id row)
     :slug        (:slug row)
     :name        (:name row)
     :description (:description row)}))

(defn create-role! [{:keys [slug name description]}]
  (jdbc/execute! db/datasource
                 ["INSERT INTO roles (id, slug, name, description, created_at, updated_at)
                   VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
                  (generate-uuid)
                  slug
                  name
                  description]))

(defn update-role! [id {:keys [slug name description]}]
  (jdbc/execute! db/datasource
                 ["UPDATE roles
                   SET slug        = ?
                     , name        = ?
                     , description = ?
                     , updated_at  = CURRENT_TIMESTAMP
                   WHERE id = ?"
                  slug
                  name
                  description
                  id]))

(defn delete-role! [id]
  (jdbc/execute! db/datasource
                 ["DELETE FROM roles WHERE id = ?" id]))

;; ---------------------------
;; Permissions
;; ---------------------------

(defn list-permissions []
  (->> (jdbc/execute! db/datasource
                      ["SELECT id, slug, name, description FROM permissions ORDER BY name"]
                      {:builder-fn rs/as-unqualified-lower-maps})
       (map (fn [row]
              {:id          (:id row)
               :slug        (:slug row)
               :name        (:name row)
               :description (:description row)}))))

(defn get-permission-by-id [id]
  (when-let [row (first (jdbc/execute! db/datasource
                                       ["SELECT id, slug, name, description FROM permissions WHERE id = ?" id]
                                       {:builder-fn rs/as-unqualified-lower-maps}))]
    {:id          (:id row)
     :slug        (:slug row)
     :name        (:name row)
     :description (:description row)}))

(defn create-permission! [{:keys [slug name description]}]
  (jdbc/execute! db/datasource
                 ["INSERT INTO permissions (id, slug, name, description, created_at, updated_at)
                   VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
                  (generate-uuid)
                  slug
                  name
                  description]))

(defn update-permission! [id {:keys [slug name description]}]
  (jdbc/execute! db/datasource
                 ["UPDATE permissions
                   SET slug        = ?
                     , name        = ?
                     , description = ?
                     , updated_at  = CURRENT_TIMESTAMP
                   WHERE id = ?"
                  slug
                  name
                  description
                  id]))

(defn delete-permission! [id]
  (jdbc/execute! db/datasource
                 ["DELETE FROM permissions WHERE id = ?" id]))

;; ---------------------------
;; Resources
;; ---------------------------

(defn list-resources []
  (->> (jdbc/execute! db/datasource
                      ["SELECT id, slug, name, description FROM resources ORDER BY name"]
                      {:builder-fn rs/as-unqualified-lower-maps})
       (map (fn [row]
              {:id          (:id row)
               :slug        (:slug row)
               :name        (:name row)
               :description (:description row)}))))

(defn get-resource-by-id [id]
  (when-let [row (first (jdbc/execute! db/datasource
                                       ["SELECT id, slug, name, description FROM resources WHERE id = ?" id]
                                       {:builder-fn rs/as-unqualified-lower-maps}))]
    {:id          (:id row)
     :slug        (:slug row)
     :name        (:name row)
     :description (:description row)}))

(defn create-resource! [{:keys [slug name description]}]
  (jdbc/execute! db/datasource
                 ["INSERT INTO resources (id, slug, name, description, created_at, updated_at)
                   VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
                  (generate-uuid)
                  slug
                  name
                  description]))

(defn update-resource! [id {:keys [slug name description]}]
  (jdbc/execute! db/datasource
                 ["UPDATE resources
                   SET slug        = ?
                     , name        = ?
                     , description = ?
                     , updated_at  = CURRENT_TIMESTAMP
                   WHERE id = ?"
                  slug
                  name
                  description
                  id]))

(defn delete-resource! [id]
  (jdbc/execute! db/datasource
                 ["DELETE FROM resources WHERE id = ?" id]))


;; ---------------------------
;; Relationships
;; ---------------------------

(defn get-roles-with-assignment-status [user-id]
  (jdbc/execute! db/datasource
                 ["SELECT
                     r.id AS role_id,
                     r.slug AS role_slug,
                     r.name AS role_name,
                     r.description AS role_description,
                     CASE
                         WHEN ur.role_id IS NOT NULL THEN 1
                         ELSE 0
                     END AS is_assigned
                   FROM roles r
                   LEFT JOIN user_roles ur ON r.id = ur.role_id AND ur.user_id = ?" user-id]
                 {:builder-fn rs/as-unqualified-lower-maps}))

(defn assign-role-to-user! [user-id role-id]
  (jdbc/execute! db/datasource
                 ["INSERT INTO user_roles (user_id, role_id, created_at, updated_at)
                   VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
                  user-id
                  role-id]))

(defn unassign-role-from-user!
  [user-id role-id]
  (jdbc/execute! db/datasource
                 ["DELETE FROM user_roles WHERE user_id = ? AND role_id = ?" user-id role-id]))

(defn get-user-permissions [user-id]
  (jdbc/execute! db/datasource
                 ["WITH user_role_permissions AS (
                     SELECT DISTINCT p.id AS permission_id, p.name AS permission_name, r.name AS role_name
                     FROM permissions p
                     JOIN role_permissions rp ON p.id = rp.permission_id
                     JOIN user_roles ur ON rp.role_id = ur.role_id
                     JOIN roles r ON ur.role_id = r.id
                     WHERE ur.user_id = ?
                   ),
                   user_direct_permissions AS (
                     SELECT DISTINCT p.id AS permission_id, p.name AS permission_name, 'Direct' AS source
                     FROM permissions p
                     JOIN user_permissions up ON p.id = up.permission_id
                     WHERE up.user_id = ? AND p.id IS NOT NULL
                   ),
                   all_permissions AS (
                     SELECT DISTINCT p.id AS permission_id, p.name AS permission_name
                     FROM permissions p
                   ),
                   unassigned_permissions AS (
                     SELECT DISTINCT ap.permission_id, ap.permission_name, 'Unassigned' AS source
                     FROM all_permissions ap
                     LEFT JOIN user_role_permissions urp ON ap.permission_id = urp.permission_id
                     LEFT JOIN user_direct_permissions udp ON ap.permission_id = udp.permission_id
                     WHERE urp.permission_id IS NULL AND udp.permission_id IS NULL
                   )
                   SELECT * FROM user_role_permissions
                   UNION ALL
                   SELECT * FROM user_direct_permissions
                   UNION ALL
                   SELECT * FROM unassigned_permissions
                   ORDER BY source, permission_name"]
                 {:builder-fn rs/as-unqualified-lower-maps}))

(defn get-permissions-with-assignment-status [user-id]
  (println "Debug: Executing get-permissions-with-assignment-status query for user-id" user-id)
  (let [query "WITH user_role_permissions AS (
                 SELECT DISTINCT p.id AS permission_id, p.name AS permission_name, 'Role - ' || r.name AS source
                 FROM permissions p
                 JOIN role_permissions rp ON p.id = rp.permission_id
                 JOIN user_roles ur ON rp.role_id = ur.role_id
                 JOIN roles r ON ur.role_id = r.id
                 WHERE ur.user_id = ?
               ),
               user_direct_permissions AS (
                 SELECT DISTINCT p.id AS permission_id, p.name AS permission_name, 'Direct' AS source
                 FROM permissions p
                 JOIN user_permissions up ON p.id = up.permission_id
                 WHERE up.user_id = ?
               ),
               all_permissions AS (
                 SELECT DISTINCT p.id AS permission_id, p.name AS permission_name
                 FROM permissions p
               ),
               unassigned_permissions AS (
                 SELECT DISTINCT ap.permission_id, ap.permission_name, 'Unassigned' AS source
                 FROM all_permissions ap
                 LEFT JOIN user_role_permissions urp ON ap.permission_id = urp.permission_id
                 LEFT JOIN user_direct_permissions udp ON ap.permission_id = udp.permission_id
                 WHERE urp.permission_id IS NULL AND udp.permission_id IS NULL
               )
               SELECT * FROM user_role_permissions
               UNION ALL
               SELECT * FROM user_direct_permissions
               UNION ALL
               SELECT * FROM unassigned_permissions
               ORDER BY source, permission_name"]
    (jdbc/execute! db/datasource [query user-id user-id] {:builder-fn rs/as-unqualified-lower-maps})))

(defn assign-permission-to-user! [user-id permission-id]
  (let [existing (jdbc/execute! db/datasource
                                ["SELECT 1 FROM user_permissions WHERE user_id = ? AND permission_id = ?"
                                 user-id
                                 permission-id])]
    (when (empty? existing)
      (jdbc/execute! db/datasource
                     ["INSERT INTO user_permissions (user_id, permission_id, created_at, updated_at)
                       VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
                      user-id
                      permission-id]))))

(defn unassign-permission-from-user! [user-id permission-id]
  (jdbc/execute! db/datasource
                 ["DELETE FROM user_permissions WHERE user_id = ? AND permission_id = ?"
                  user-id
                  permission-id]))

;; ---------------------------
;; Role-Permission Relationships
;; ---------------------------

(defn get-permissions-with-assignment-status-for-role [role-id]
  (println "Debug: Executing get-permissions-with-assignment-status-for-role query for role-id" role-id)
  (let [query "WITH role_permissions_for_role AS (
                 SELECT DISTINCT p.id AS permission_id, p.name AS permission_name, 'Assigned' AS source
                 FROM permissions p
                 JOIN role_permissions rp ON p.id = rp.permission_id
                 WHERE rp.role_id = ?
               ),
               all_permissions AS (
                 SELECT DISTINCT p.id AS permission_id, p.name AS permission_name
                 FROM permissions p
               ),
               unassigned_permissions AS (
                 SELECT DISTINCT ap.permission_id, ap.permission_name, 'Unassigned' AS source
                 FROM all_permissions ap
                 LEFT JOIN role_permissions rp ON ap.permission_id = rp.permission_id AND rp.role_id = ?
                 WHERE rp.permission_id IS NULL
               )
               SELECT * FROM role_permissions_for_role
               UNION ALL
               SELECT * FROM unassigned_permissions
               ORDER BY source, permission_name"]
    (let [result (jdbc/execute! db/datasource [query role-id role-id] {:builder-fn rs/as-unqualified-lower-maps})]
      (println "Debug: Query result" result)
      result)))

(defn assign-permission-to-role! [role-id permission-id]
  (println "Debug: Assigning permission" permission-id "to role" role-id)
  (jdbc/execute! db/datasource
                 ["INSERT INTO role_permissions (role_id, permission_id, created_at, updated_at)
                  VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
                  role-id permission-id]))

(defn unassign-permission-from-role! [role-id permission-id]
  (println "Debug: Unassigning permission" permission-id "from role" role-id)
  (jdbc/execute! db/datasource
                 ["DELETE FROM role_permissions WHERE role_id = ? AND permission_id = ?"
                  role-id permission-id]))