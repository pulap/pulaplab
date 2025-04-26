(ns pulaplab.auth.ui.pages
  (:require [pulaplab.ui.layout :refer [layout]]
            [pulaplab.ui.styles :as styles]))

(defn list-users-page [users flash]
  (layout
   {:title "User List"
    :header-content
    [:ul {:class "flex space-x-4"}
     [:li [:a {:href "/private/auth/list-users" :class "text-white"} "Users"]]
     [:li [:a {:href "/private/auth/list-roles" :class "text-white"} "Roles"]]
     [:li [:a {:href "/private/auth/list-permissions" :class "text-white"} "Permissions"]]
     [:li [:a {:href "/private/auth/list-resources" :class "text-white"} "Resources"]]
     [:li [:a {:href "/something" :class "text-white"} "Pulap"]]]

    :main-content
    [:div {:class "space-y-8"}
     [:h1 {:class "text-2xl font-bold mb-4"} "User List"]
     [:table {:class "min-w-full divide-y divide-gray-200"}
      [:thead {:class "bg-gray-50"}
       [:tr
        [:th {:class "px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/3"} "Username"]
        [:th {:class "px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/3"} "Email"]
        [:th {:class "px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-1/3"} "Actions"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [{:keys [id username email]} users]
         [:tr {:key id}
          [:td {:class "px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900"}
           [:a {:href (str "/auth/show-user?id=" id) :class "text-blue-500 hover:underline"} username]]
          [:td {:class "px-6 py-4 whitespace-nowrap text-sm text-gray-500"} email]
          [:td {:class "px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-center space-x-2"}
           [:a {:href (str "/auth/show-user?id=" id)
                :class "inline-block bg-green-500 text-white px-6 py-2 rounded w-24"} "Show"]
           [:a {:href (str "/auth/edit-user?id=" id)
                :class "inline-block bg-yellow-500 text-white px-6 py-2 rounded w-24"} "Edit"]
           [:form {:action "/auth/delete-user" :method "POST" :class "inline"}
            [:input {:type "hidden" :name "id" :value id}]
            [:button {:type "submit" :class "inline-block bg-red-500 text-white px-6 py-2 rounded w-24"}
             "Delete"]]]])]]
     ;; Botón New justo después de la tabla
     [:div {:class "flex justify-center mt-6"}
      [:a {:href "/private/auth/new-user"
           :class "bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"}
       "New"]]]

    :footer-content
    [:p "&copy; 2025 Pulap"]}))
