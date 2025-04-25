#!/bin/bash

mkdir -p resources/migrations

cat > resources/migrations/20250426004100-create-users-table.sql <<EOF
-- Migration: create-users-table
-- +migrate Up
CREATE TABLE users (
    id TEXT PRIMARY KEY,
    name TEXT,
    username TEXT,
    email_enc BLOB,
    password_enc BLOB,
    slug TEXT,
    created_by TEXT,
    updated_by TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    last_login_at TIMESTAMP,
    last_login_ip TEXT,
    is_active BOOLEAN DEFAULT 1
);

CREATE INDEX idx_users_email_enc ON users(email_enc);

-- +migrate Down
DROP TABLE users;
EOF

cat > resources/migrations/20250426004223-create-roles-table.sql <<EOF
-- Migration: create-roles-table
-- +migrate Up
CREATE TABLE roles (
    id TEXT PRIMARY KEY,
    slug TEXT,
    name TEXT,
    description TEXT,
    status TEXT,
    created_by TEXT,
    updated_by TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- +migrate Down
DROP TABLE roles;
EOF

cat > resources/migrations/20250426004341-create-permissions-table.sql <<EOF
-- Migration: create-permissions-table
-- +migrate Up
CREATE TABLE permissions (
    id TEXT PRIMARY KEY,
    slug TEXT,
    name TEXT,
    description TEXT,
    created_by TEXT,
    updated_by TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- +migrate Down
DROP TABLE permissions;
EOF

cat > resources/migrations/20250426004509-create-resources-table.sql <<EOF
-- Migration: create-resources-table
-- +migrate Up
CREATE TABLE resources (
    id TEXT PRIMARY KEY,
    slug TEXT,
    name TEXT,
    description TEXT,
    label TEXT,
    type TEXT,
    uri TEXT,
    created_by TEXT,
    updated_by TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- +migrate Down
DROP TABLE resources;
EOF

cat > resources/migrations/20250426004631-create-user-roles-table.sql <<EOF
-- Migration: create-user-roles-table
-- +migrate Up
CREATE TABLE user_roles (
    user_id TEXT,
    role_id TEXT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- +migrate Down
DROP TABLE user_roles;
EOF

cat > resources/migrations/20250426004757-create-role-permissions-table.sql <<EOF
-- Migration: create-role-permissions-table
-- +migrate Up
CREATE TABLE role_permissions (
    role_id TEXT,
    permission_id TEXT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);

-- +migrate Down
DROP TABLE role_permissions;
EOF

cat > resources/migrations/20250426004915-create-user-permissions-table.sql <<EOF
-- Migration: create-user-permissions-table
-- +migrate Up
CREATE TABLE user_permissions (
    user_id TEXT,
    permission_id TEXT,
    PRIMARY KEY (user_id, permission_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_permissions_user_id ON user_permissions(user_id);
CREATE INDEX idx_user_permissions_permission_id ON user_permissions(permission_id);

-- +migrate Down
DROP TABLE user_permissions;
EOF

cat > resources/migrations/20250426005048-create-resource-permissions-table.sql <<EOF
-- Migration: create-resource-permissions-table
-- +migrate Up
CREATE TABLE resource_permissions (
    resource_id TEXT NOT NULL,
    permission_id TEXT NOT NULL,
    created_by TEXT,
    created_at TIMESTAMP,
    PRIMARY KEY (resource_id, permission_id),
    FOREIGN KEY (resource_id) REFERENCES resources(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- +migrate Down
DROP TABLE resource_permissions;
EOF

