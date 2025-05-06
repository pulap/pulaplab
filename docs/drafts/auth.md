# Authorization System Design for Pulap

## Context
Pulap is a Real Estate Management application implemented in Clojure. While the focus here is on its authentication and authorization system, this is just one part of the broader platform. See this document as a draft of a future Architecture Decision Record (ADR).

## Status
Draft

## Decision
To implement a hybrid ABAC (Attribute-Based Access Control) + RBAC (Role-Based Access Control) system for managing access to resources within the application. This system will:

1. Intercept every HTTP request to verify if the logged-in user has the necessary permissions to access a resource.
2. Associate resources with paths (e.g., `/admin`, `/auth/list-users`) and manage permissions through roles and direct assignments.
3. Cache user permissions in session storage to optimize performance while ensuring security through session invalidation when permissions change.

## Key Concepts

### Hybrid ABAC + RBAC
- **RBAC (Role-Based Access Control):**
  - Users are assigned roles.
  - Roles define permissions for accessing resources.
- **ABAC (Attribute-Based Access Control):**
  - Access decisions are based on attributes (e.g., user, resource, or environment).
- **Hybrid Approach:**
  - Combines RBAC for structured permissions and ABAC for granular control.

### Resource Management
- Resources are defined in the `pulaplab.auth.resource` namespace.
- Each resource has attributes like `slug`, `name`, `description`, `label`, `type`, and `path` (formerly `uri`).

#### Example Resource Definition:
```clojure
(ns auth.resource
  (:require [pulaplab.model.core :as base]))

(defn new-resource
  [{:keys [slug name description label type path]
    :or {slug nil
         label nil
         type nil
         path nil}}]
  (merge (base/base-attrs)
         {:slug slug
          :name name
          :description description
          :label label
          :type type
          :path path}))
```

### Middleware for Authorization
- A middleware intercepts each request to:
  1. Extract the path from the request.
  2. Retrieve the associated resource and its required permissions.
  3. Compare the user's permissions (from session or database) with the required permissions.

#### Example Middleware:
```clojure
(defn auth-middleware [handler]
  (fn [request]
    (let [user (:user request) ; Simulated logged-in user
          path (:uri request)
          required-permissions (get path-permissions path #{})]
      (if (every? #(contains? (:permissions user) %) required-permissions)
        (handler request)
        {:status 403
         :body "Forbidden"}))))
```

### Session Management
- User permissions can be cached in session storage (e.g., cookies or local storage) to avoid frequent database queries.
- A token-based approach can validate sessions efficiently.

#### Example Token Validation:
```clojure
(def session-tokens (atom {}))

(defn validate-session [token]
  (get @session-tokens token))
```

### Invalidating Sessions
- When permissions change, the session token can be invalidated to force the user to reauthenticate.

#### Example Invalidating a Token:
```clojure
(defn invalidate-session [token]
  (swap! session-tokens dissoc token))
```

## Optimizations

### Caching Permissions
- Store user permissions in memory or cookies to avoid frequent database queries.

### Session Token Validation
- Use a lightweight token validation mechanism to ensure fast request processing.

### Immediate Invalidations
- Implement a mechanism to invalidate sessions when permissions change, ensuring security in critical scenarios.

## Future Considerations

### Real-Time Updates
- Use WebSockets or Server-Sent Events (SSE) to notify clients of permission changes.

### Extending to ABAC
- Add support for attribute-based rules (e.g., time of access, resource ownership).

### Scalability
- If the app grows to support multiple tenants or instances, consider distributed caching solutions like Hazelcast.

## Summary
This document lays out a plan for building a secure authorization system in Pulap.