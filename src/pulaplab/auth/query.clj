(ns pulaplab.auth.query
  (:require
   [clojure.tools.logging :as log]))

(def queries
  {;; Users CRUD
   :list-users "SELECT id, username, email_enc FROM users ORDER BY username"
   :get-user-by-id "SELECT id, username, email_enc FROM users WHERE id = ?"
   :create-user "INSERT INTO users (id, username, email_enc, created_at, updated_at, is_active) VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)"
   :update-user "UPDATE users SET username = ?, email_enc = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?"
   :delete-user "DELETE FROM users WHERE id = ?"

   ;; Roles CRUD
   :list-roles "SELECT id, slug, name, description FROM roles ORDER BY name"
   :get-role-by-id "SELECT id, slug, name, description FROM roles WHERE id = ?"
   :create-role "INSERT INTO roles (id, slug, name, description, created_at, updated_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
   :update-role "UPDATE roles SET slug = ?, name = ?, description = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?"
   :delete-role "DELETE FROM roles WHERE id = ?"

   ;; Permissions CRUD
   :list-permissions "SELECT id, slug, name, description FROM permissions ORDER BY name"
   :get-permission-by-id "SELECT id, slug, name, description FROM permissions WHERE id = ?"
   :create-permission "INSERT INTO permissions (id, slug, name, description, created_at, updated_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
   :update-permission "UPDATE permissions SET slug = ?, name = ?, description = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?"
   :delete-permission "DELETE FROM permissions WHERE id = ?"

   ;; Resources CRUD
   :list-resources "SELECT id, slug, name, description FROM resources ORDER BY name"
   :get-resource-by-id "SELECT id, slug, name, description FROM resources WHERE id = ?"
   :create-resource "INSERT INTO resources (id, slug, name, description, created_at, updated_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
   :update-resource "UPDATE resources SET slug = ?, name = ?, description = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?"
   :delete-resource "DELETE FROM resources WHERE id = ?"

   ;; User roles related
   :get-user-roles "SELECT role_id FROM user_roles WHERE user_id = ?"
   :get-roles-with-assignment-status "SELECT r.id AS id, r.slug AS slug, r.name AS name, CASE WHEN ur.user_id IS NOT NULL THEN 'Assigned' ELSE 'Unassigned' END AS status FROM roles r LEFT JOIN user_roles ur ON r.id = ur.role_id AND ur.user_id = ? ORDER BY status, r.name"
   :assign-role-to-user "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)"
   :unassign-role-from-user "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?"

   ;; User permissions related
   :get-permissions-with-assignment-status-for-user "WITH user_permissions_for_user AS (
                 SELECT DISTINCT p.id AS permission_id, p.name AS permission_name, 'Assigned' AS source
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
                 LEFT JOIN user_permissions up ON ap.permission_id = up.permission_id AND up.user_id = ?
                 WHERE up.permission_id IS NULL
               )
               SELECT * FROM user_permissions_for_user
               UNION ALL
               SELECT * FROM unassigned_permissions
               ORDER BY source, permission_name"
   :assign-permission-to-user "INSERT INTO user_permissions (user_id, permission_id) VALUES (?, ?)"
   :unassign-permission-from-user "DELETE FROM user_permissions WHERE user_id = ? AND permission_id = ?"

   ;; Role permissions related
   :get-permissions-with-assignment-status-for-role "WITH role_permissions_for_role AS (
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
               ORDER BY source, permission_name"
   :assign-permission-to-role "INSERT INTO role_permissions (role_id, permission_id, created_at, updated_at)
                  VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
   :unassign-permission-from-role "DELETE FROM role_permissions WHERE role_id = ? AND permission_id = ?"
   :get-role-permissions "SELECT permission_id FROM role_permissions WHERE role_id = ?"

   ;; Resource permissions related
   :get-permissions-with-assignment-status-for-resource "WITH resource_permissions_for_resource AS (
                 SELECT DISTINCT p.id AS permission_id, p.name AS permission_name, 'Assigned' AS source
                 FROM permissions p
                 JOIN resource_permissions rp ON p.id = rp.permission_id
                 WHERE rp.resource_id = ?
               ),
               all_permissions AS (
                 SELECT DISTINCT p.id AS permission_id, p.name AS permission_name
                 FROM permissions p
               ),
               unassigned_permissions AS (
                 SELECT DISTINCT ap.permission_id, ap.permission_name, 'Unassigned' AS source
                 FROM all_permissions ap
                 LEFT JOIN resource_permissions rp ON ap.permission_id = rp.permission_id AND rp.resource_id = ?
                 WHERE rp.permission_id IS NULL
               )
               SELECT * FROM resource_permissions_for_resource
               UNION ALL
               SELECT * FROM unassigned_permissions
               ORDER BY source, permission_name"
   :assign-permission-to-resource "INSERT INTO resource_permissions (resource_id, permission_id) VALUES (?, ?) ON CONFLICT DO NOTHING"
   :unassign-permission-from-resource "DELETE FROM resource_permissions WHERE resource_id = ? AND permission_id = ?"
   :get-resources-with-permission "WITH assigned_resources AS (
                 SELECT DISTINCT r.id AS resource_id, r.name AS resource_name, 'Assigned' AS status
                 FROM resources r
                 JOIN resource_permissions rp ON r.id = rp.resource_id
                 WHERE rp.permission_id = ?
               ),
               all_resources AS (
                 SELECT DISTINCT r.id AS resource_id, r.name AS resource_name
                 FROM resources r
               ),
               unassigned_resources AS (
                 SELECT DISTINCT ar.resource_id, ar.resource_name, 'Unassigned' AS status
                 FROM all_resources ar
                 LEFT JOIN resource_permissions rp ON ar.resource_id = rp.resource_id AND rp.permission_id = ?
                 WHERE rp.resource_id IS NULL
               )
               SELECT * FROM assigned_resources
               UNION ALL
               SELECT * FROM unassigned_resources
               ORDER BY status, resource_name"
   :get-resource-permissions "SELECT permission_id FROM resource_permissions WHERE resource_id = ?"

   ;; Other
   :get-user-by-username "SELECT id, username, email_enc FROM users WHERE username = ?"})


(defn get-query
  "Retrieve SQL statement by key, log error and throw if not found."
  [k]
  (if-let [sql (get queries k)]
    sql
    (do
      (log/debug "[ERROR] No SQL query defined for key:" k)
      (throw (ex-info (str "Missing SQL query for key: " k) {:key k})))))