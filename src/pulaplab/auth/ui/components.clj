(ns pulaplab.auth.ui.components
  (:require [pulaplab.ui.styles :as styles]))

(defn flash-message [msg]
  (when msg
    [:div {:class (styles/classes :flash-success)}
     msg]))

(defn header [title]
  [:header
   [:h1 {:class (styles/classes :page-header)} title]])

(defn menu []
  [:nav
   [:ul {:class (styles/classes :nav-list)}
    [:li [:a {:href "/private/auth/list-users" :class (styles/classes :nav-button)} "Users"]]
    [:li [:a {:href "/private/auth/list-roles" :class (styles/classes :nav-button)} "Roles"]]
    [:li [:a {:href "/private/auth/list-permissions" :class (styles/classes :nav-button)} "Permissions"]]
    [:li [:a {:href "/private/auth/list-resources" :class (styles/classes :nav-button)} "Resources"]]]])
