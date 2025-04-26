(ns pulaplab.auth.service
  (:require [clojure.set :as set]))

;; Relaciones in-memory (por ahora)
(def user-roles (atom {}))          ;; {user-id #{role-id}}
(def user-permissions (atom {}))    ;; {user-id #{permission-id}}
(def role-permissions (atom {}))    ;; {role-id #{permission-id}}
(def resource-permissions (atom {}));; {resource-id #{permission-id}}

(defn assign-role-to-user! [user-id role-id]
  (swap! user-roles update user-id (fnil conj #{}) role-id))

(defn assign-permission-to-user! [user-id permission-id]
  (swap! user-permissions update user-id (fnil conj #{}) permission-id))

(defn assign-permission-to-role! [role-id permission-id]
  (swap! role-permissions update role-id (fnil conj #{}) permission-id))

(defn assign-permission-to-resource! [resource-id permission-id]
  (swap! resource-permissions update resource-id (fnil conj #{}) permission-id))

(defn get-user-effective-permissions [user-id]
  (let [roles (get @user-roles user-id #{})
        perms-from-roles (reduce
                          (fn [acc role-id]
                            (set/union acc (get @role-permissions role-id #{})))
                          #{}
                          roles)
        direct-perms (get @user-permissions user-id #{})]
    (set/union perms-from-roles direct-perms)))

(defn can-access-resource? [user-id resource-id]
  (let [user-perms (get-user-effective-permissions user-id)
        resource-perms (get @resource-permissions resource-id #{})]
    (not (empty? (set/intersection user-perms resource-perms)))))
