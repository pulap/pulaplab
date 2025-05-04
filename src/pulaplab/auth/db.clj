(ns pulaplab.auth.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [pulaplab.db.core :as db]
            [clojure.string :as str])
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
;; Resounces
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
