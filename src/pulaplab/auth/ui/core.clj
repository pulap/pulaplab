(ns pulaplab.auth.ui.core
  (:require
   [pulaplab.ui.styles     :as styles]
   [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn form
  [attrs & children]
  (into
   [:form attrs (anti-forgery-field)]
   children))

(defn header
  []
  [:ul {:class (styles/get-class :menu-list)}
   [:li [:a {:href "/private/auth/list-users"
             :class (styles/get-class :header-link)}
         "Users"]]
   [:li [:a {:href "/private/auth/list-roles"
             :class (styles/get-class :header-link)}
         "Roles"]]
   [:li [:a {:href "/private/auth/list-permissions"
             :class (styles/get-class :header-link)}
         "Permissions"]]
   [:li [:a {:href "/private/auth/list-resources"
             :class (styles/get-class :header-link)}
         "Resources"]]
   [:li [:a {:href "/res/todo"
             :class (styles/get-class :header-link)}
         "Todo"]]])

(defn footer
  []
  [:p "© 2025 Pulap"])