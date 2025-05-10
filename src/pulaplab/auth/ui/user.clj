(ns pulaplab.auth.ui.user
  (:require
   [pulaplab.ui.layout     :refer [layout]]
   [pulaplab.ui.styles     :as styles]
   [pulaplab.auth.ui.core  :as core]))

;; Use the predefined `user-views` function from `pulaplab.ui.styles`
(def sc (styles/user-views))

(defn index
  [users flash]
  (layout
   {:title          "User List"
    :header-content (core/header)
    :main-content
    [:div {:class "space-y-8"}
     (when flash
       [:div {:class "bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded"}
        flash])
     [:h1 {:class "text-2xl font-bold mb-4"} "User List"]
     [:table {:class (:table sc)}
      [:thead {:class (:thead sc)}
       [:tr
        [:th {:class (:th sc)} "Username"]
        [:th {:class (:th sc)} "Email"]
        [:th {:class (:th-actions sc)} "Actions"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [{:keys [id username email]} users]
         [:tr {:key id}
          [:td {:class (:td-primary sc)}
           [:a {:href  (str "/private/auth/show-user?id=" id)
                :class (:link-primary sc)}
            username]]
          [:td {:class (:td-secondary sc)} email]
          [:td {:class (:td-actions sc)}
           [:a {:href  (str "/private/auth/show-user?id=" id)
                :class (:button-show sc)} "Show"]
           [:a {:href  (str "/private/auth/edit-user?id=" id)
                :class (:button-edit sc)} "Edit"]
           (core/form {:action "/private/auth/delete-user" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "id" :value id}]
                      [:button {:type  "submit" :class (:button-delete sc)}
                       "Delete"])]])]]
     [:div {:class "flex justify-center mt-6"}
      [:a {:href "/private/auth/new-user"
           :class (:button-new sc)}
       "New"]]]
    :footer-content (core/footer)}))

(defn new
  []
  (layout
   {:title          "New User"
    :header-content (core/header)
    :main-content
    [:div {:class "flex justify-center"}
     [:div {:class "bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"}
      [:h1 {:class "text-2xl font-bold mb-6 text-center"} "Create New User"]
      (core/form {:action "/private/auth/create-user"
                  :method "POST"
                  :class  "space-y-6"}
                 [:input {:type  "hidden" :name "id" :value ""}]
                 [:div
                  [:label {:for   "username"
                           :class (:form-label sc)}
                   "Username"]
                  [:input {:type     "text"
                           :name     "username"
                           :id       "username"
                           :required true
                           :class    (:form-input sc)}]]
                 [:div
                  [:label {:for   "email"
                           :class (:form-label sc)}
                   "Email"]
                  [:input {:type     "email"
                           :name     "email"
                           :id       "email"
                           :required true
                           :class    (:form-input sc)}]]
                 [:div
                  [:label {:for   "name"
                           :class (:form-label sc)}
                   "Name"]
                  [:input {:type  "text"
                           :name  "name"
                           :id    "name"
                           :class (:form-input sc)}]]
                 [:div
                  [:label {:for   "password"
                           :class (:form-label sc)}
                   "Password"]
                  [:input {:type     "password"
                           :name     "password"
                           :id       "password"
                           :required true
                           :class    (:form-input sc)}]]
                 [:div
                  [:label {:for   "password_conf"
                           :class (:form-label sc)}
                   "Confirm Password"]
                  [:input {:type     "password"
                           :name     "password_conf"
                           :id       "password_conf"
                           :required true
                           :class    (:form-input sc)}]]
                 [:div {:class "flex justify-center mt-6 space-x-4"}
                  [:a {:href  "/private/auth/list-users"
                       :class (:cancel-button sc)}
                   "Back"]
                  [:button {:type  "submit"
                            :class (:button-new sc)}
                   "Create"]])]]
    :footer-content (core/footer)}))

(defn show
  [user]
  (layout
   {:title          "Show User"
    :header-content (core/header)
    :main-content
    [:div {:class "flex justify-center"}
     [:div {:class "bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"}
      [:h1 {:class "text-2xl font-bold mb-6 text-center"} "User Details"]
      [:div {:class "space-y-4"}
       [:div
        [:label {:class (:form-label sc)} "Username"]
        [:p   {:class "mt-1 text-gray-900"} (:username user)]]
       [:div
        [:label {:class (:form-label sc)} "Email"]
        [:p   {:class "mt-1 text-gray-900"} (:email user)]]
       (when-let [name (:name user)]
         [:div
          [:label {:class (:form-label sc)} "Name"]
          [:p   {:class "mt-1 text-gray-900"} name]])]
      [:div {:class "flex justify-center mt-6 space-x-4"}
       [:a {:href  "/private/auth/list-users"
            :class (:cancel-button sc)}
        "Back"]
       [:a {:href  (str "/private/auth/list-user-roles?id=" (:id user))
            :class (:button-new sc)}
        "Roles"]
       [:a {:href  (str "/private/auth/list-user-permissions?id=" (:id user))
            :class (:button-new sc)}
        "Permissions"]]]]
    :footer-content (core/footer)}))


(defn edit
  [user]
  (layout
   {:title          "Edit User"
    :header-content (core/header)
    :main-content
    [:div {:class (:container sc)}
     [:h1 {:class "text-2xl font-bold mb-4"} "Edit User"]
     (core/form {:action (str "/private/auth/update-user?id=" (:id user))
                 :method "POST"
                 :class  "space-y-6"}
                [:input {:type "hidden" :name "id" :value (:id user)}]
                [:div
                 [:label {:for   "username"
                          :class (:form-label sc)}
                  "Username"]
                 [:input {:type     "text"
                          :name     "username"
                          :id       "username"
                          :value    (:username user)
                          :required true
                          :class    (:form-input sc)}]]
                [:div
                 [:label {:for   "email"
                          :class (:form-label sc)}
                  "Email"]
                 [:input {:type     "email"
                          :name     "email"
                          :id       "email"
                          :value    (:email user)
                          :required true
                          :class    (:form-input sc)}]]
                [:div {:class "flex justify-end space-x-4"}
                 [:a {:href  "/private/auth/list-users"
                      :class (:cancel-button sc)}
                  "Cancel"]
                 [:button {:type  "submit"
                           :class (:button-new sc)}
                  "Update"]])]
    :footer-content (core/footer)}))
