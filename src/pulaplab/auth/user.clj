(ns pulaplab.auth.user
  (:require [pulaplab.model.core :as base]))

(defn new-user
  [{:keys [username email_enc password_enc slug last-login-at last-login-ip is-active]
    :or {slug nil
         last-login-at nil
         last-login-ip nil
         is-active true}}]
  (merge (base/base-attrs)
         {:username username
          :email_enc email_enc
          :password_enc password_enc
          :slug slug
          :last_login_at last-login-at
          :last_login_ip last-login-ip
          :is_active is-active}))
