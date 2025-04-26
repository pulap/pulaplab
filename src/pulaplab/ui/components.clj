(ns pulaplab.auth.ui.components)

(defn flash-message [msg]
  (when msg
    [:div {:class "bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative"}
     msg]))

(defn header [title]
  [:header {:class "mb-6"}
   [:h1 {:class "text-3xl font-bold text-gray-800"} title]])

(defn menu []
  [:nav {:class "mb-6"}
   [:ul {:class "flex space-x-4"}
    [:li [:a {:href "/auth/users" :class "text-blue-600 hover:underline"} "Users"]]
    [:li [:a {:href "/auth/roles" :class "text-blue-600 hover:underline"} "Roles"]]
    [:li [:a {:href "/auth/permissions" :class "text-blue-600 hover:underline"} "Permissions"]]
    [:li [:a {:href "/auth/resources" :class "text-blue-600 hover:underline"} "Resources"]]]])
