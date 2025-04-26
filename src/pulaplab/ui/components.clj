(ns pulaplab.ui.components
  (:require
   [pulaplab.ui.styles     :as styles]
   [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn flash-message
  "Render a flash message if `msg` is present."
  [msg]
  (when msg
    [:div {:class (styles/get-class :flash-success)}
     msg]))

(defn header
  "Render a page header with given `title`."
  [title]
  [:header
   [:h1 {:class (styles/get-class :page-header)} title]])

(defn menu
  "Render the main navigation menu."
  []
  [:nav
   [:ul {:class (styles/get-class :menu-list)}
    [:li [:a {:href "/private/auth/list-users"
              :class (styles/get-class :header-link)} "Users"]]
    [:li [:a {:href "/private/auth/list-roles"
              :class (styles/get-class :header-link)} "Roles"]]
    [:li [:a {:href "/private/auth/list-permissions"
              :class (styles/get-class :header-link)} "Permissions"]]
    [:li [:a {:href "/private/auth/list-resources"
              :class (styles/get-class :header-link)} "Resources"]]]])

(defn form
  "Replaces [:form attrs & children] and injects a hidden CSRF token field."
  [attrs & children]
  (into
   [:form attrs
    (anti-forgery-field)]
   children))
