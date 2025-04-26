(ns pulaplab.auth.ui.pages
  (:require
   [pulaplab.ui.layout     :refer [layout]]
   [pulaplab.ui.styles     :as styles]
   [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn form
  [attrs & children]
  (into
   [:form attrs (anti-forgery-field)]
   children))

(defn default-header
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

(defn default-footer
  []
  [:p "© 2025 Pulap"])


(defn list-users-page
  [users flash]
  (layout
   {:title          "User List"
    :header-content (default-header)
    :main-content
    [:div {:class "space-y-8"}
     (when flash
       [:div {:class "bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded"}
        flash])
     [:h1 {:class "text-2xl font-bold mb-4"} "User List"]
     [:table {:class (styles/get-class :table)}
      [:thead {:class (styles/get-class :thead)}
       [:tr
        [:th {:class (styles/get-class :th)} "Username"]
        [:th {:class (styles/get-class :th)} "Email"]
        [:th {:class (styles/get-class :th)} "Actions"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [{:keys [id username email]} users]
         [:tr {:key id}
          [:td {:class (styles/get-class :td-username)}
           [:a {:href  (str "/private/auth/show-user?id=" id)
                :class (styles/get-class :link-username)}
            username]]
          [:td {:class (styles/get-class :td-email)} email]
          [:td {:class (styles/get-class :td-actions)}
           [:a {:href  (str "/private/auth/show-user?id=" id)
                :class (styles/get-class :button-show)}
            "Show"]
           [:a {:href  (str "/private/auth/edit-user?id=" id)
                :class (styles/get-class :button-edit)}
            "Edit"]
           (form {:action "/private/auth/delete-user"
                  :method "POST"
                  :class  "inline"}
                 [:input  {:type  "hidden" :name "id" :value id}]
                 [:button {:type  "submit"
                           :class (styles/get-class :button-delete)}
                  "Delete"])]])]]
     [:div {:class "flex justify-center mt-6"}
      [:a {:href  "/private/auth/new-user"
           :class (styles/get-class :button-new)}
       "New"]]]
    :footer-content (default-footer)}))

(defn new-user-page
  []
  (layout
   {:title          "New User"
    :header-content (default-header)
    :main-content
    [:div {:class "flex justify-center"}
     [:div {:class "bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"}
      [:h1 {:class "text-2xl font-bold mb-6 text-center"} "Create New User"]
      (form {:action "/private/auth/create-user"
             :method "POST"
             :class  "space-y-6"}
            [:input {:type  "hidden" :name "id" :value ""}]
            [:div
             [:label {:for   "username"
                      :class (styles/get-class :form-label)}
              "Username"]
             [:input {:type     "text"
                      :name     "username"
                      :id       "username"
                      :required true
                      :class    (styles/get-class :form-input)}]]
            [:div
             [:label {:for   "email"
                      :class (styles/get-class :form-label)}
              "Email"]
             [:input {:type     "email"
                      :name     "email"
                      :id       "email"
                      :required true
                      :class    (styles/get-class :form-input)}]]
            [:div
             [:label {:for   "name"
                      :class (styles/get-class :form-label)}
              "Name"]
             [:input {:type  "text"
                      :name  "name"
                      :id    "name"
                      :class (styles/get-class :form-input)}]]
            [:div
             [:label {:for   "password"
                      :class (styles/get-class :form-label)}
              "Password"]
             [:input {:type     "password"
                      :name     "password"
                      :id       "password"
                      :required true
                      :class    (styles/get-class :form-input)}]]
            [:div
             [:label {:for   "password_conf"
                      :class (styles/get-class :form-label)}
              "Confirm Password"]
             [:input {:type     "password"
                      :name     "password_conf"
                      :id       "password_conf"
                      :required true
                      :class    (styles/get-class :form-input)}]]
            [:div {:class "flex justify-center mt-6 space-x-4"}
             [:a {:href  "/private/auth/list-users"
                  :class (styles/get-class :cancel-button)}
              "Back"]
             [:button {:type  "submit"
                       :class (styles/get-class :button-new)}
              "Create"]])]]
    :footer-content (default-footer)}))

(defn show-user-page
  [user]
  (layout
   {:title          "Show User"
    :header-content (default-header)
    :main-content
    [:div {:class "flex justify-center"}
     [:div {:class "bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"}
      [:h1 {:class "text-2xl font-bold mb-6 text-center"} "User Details"]
      [:div {:class "space-y-4"}
       [:div
        [:label {:class (styles/get-class :form-label)} "Username"]
        [:p {:class "mt-1 text-gray-900"} (:username user)]]
       [:div
        [:label {:class (styles/get-class :form-label)} "Email"]
        [:p {:class "mt-1 text-gray-900"} (:email user)]]
       (when-let [name (:name user)]
         [:div
          [:label {:class (styles/get-class :form-label)} "Name"]
          [:p {:class "mt-1 text-gray-900"} name]])]
      [:div {:class "flex justify-center mt-6"}
       [:a {:href  "/private/auth/list-users"
            :class (styles/get-class :cancel-button)}
        "Back"]]]]
    :footer-content (default-footer)}))



(defn edit-user-page
  [user]
  (layout
   {:title          "Edit User"
    :header-content (default-header)
    :main-content
    [:div {:class (styles/get-class :container)}
     [:h1 {:class "text-2xl font-bold mb-4"} "Edit User"]
     (form {:action (str "/private/auth/update-user?id=" (:id user))
            :method "POST"
            :class  "space-y-6"}
           [:div
            [:label {:for   "username"
                     :class (styles/get-class :form-label)}
             "Username"]
            [:input {:type     "text"
                     :name     "username"
                     :id       "username"
                     :value    (:username user)
                     :required true
                     :class    (styles/get-class :form-input)}]]
           [:div
            [:label {:for   "email"
                     :class (styles/get-class :form-label)}
             "Email"]
            [:input {:type     "email"
                     :name     "email"
                     :id       "email"
                     :value    (:email user)
                     :required true
                     :class    (styles/get-class :form-input)}]]
           [:div {:class "flex justify-end space-x-4"}
            [:a {:href  "/private/auth/list-users"
                 :class (styles/get-class :cancel-button)}
             "Cancel"]
            [:button {:type  "submit"
                      :class (styles/get-class :button-new)}
             "Update"]])]
    :footer-content (default-footer)}))

