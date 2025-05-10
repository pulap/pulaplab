(ns pulaplab.auth.ui.role
  (:require [pulaplab.ui.layout :refer [layout]]
            [pulaplab.ui.styles :as styles]
            [pulaplab.auth.ui.core :as core]))

;; Use the predefined `role-views` function from `pulaplab.ui.styles`
(def sc (styles/role-views))

(defn index
  [roles flash]
  (layout
   {:title          "Role List"
    :header-content (core/header)
    :main-content
    [:div {:class "space-y-8"}
     (when flash
       [:div {:class "bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded"}
        flash])
     [:h1 {:class "text-2xl font-bold mb-4"} "Role List"]
     [:table {:class (sc :table)}
      [:thead {:class (sc :thead)}
       [:tr
        [:th {:class (sc :th)} "Name"]
        [:th {:class (sc :th)} "Description"]
        [:th {:class (sc :th-actions)} "Actions"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [{:keys [id name description]} roles]
         [:tr {:key id}
          [:td {:class (sc :td-primary)}
           [:a {:href  (str "/private/auth/show-role?id=" id)
                :class (sc :link-primary)}
            name]]
          [:td {:class (sc :td-secondary)}
           description]
          [:td {:class (sc :td-actions)}
           [:a {:href  (str "/private/auth/show-role?id=" id)
                :class (sc :button-show)} "Show"]
           [:a {:href  (str "/private/auth/edit-role?id=" id)
                :class (sc :button-edit)} "Edit"]
           (core/form {:action "/private/auth/delete-role" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "id" :value id}]
                      [:button {:type  "submit" :class (sc :button-delete)}
                       "Delete"])]])]]
     [:div {:class "flex justify-center mt-6"}
      [:a {:href "/private/auth/new-role"
           :class (sc :button-new)}
       "New"]]]
    :footer-content (core/footer)}))

(defn new
  []
  (layout
   {:title          "New Role"
    :header-content (core/header)
    :main-content
    [:div {:class "flex justify-center"}
     [:div {:class "bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"}
      [:h1 {:class "text-2xl font-bold mb-6 text-center"} "Create New Role"]
      (core/form {:action "/private/auth/create-role"
                  :method "POST"
                  :class  "space-y-6"}
                 [:input {:type  "hidden" :name "id" :value ""}]
                 [:div
                  [:label {:for   "name" :class (sc :form-label)} "Name"]
                  [:input {:type     "text"
                           :name     "name"
                           :id       "name"
                           :required true
                           :class    (sc :form-input)}]]
                 [:div
                  [:label {:for   "description" :class (sc :form-label)} "Description"]
                  [:textarea {:name  "description"
                              :id    "description"
                              :class (sc :form-input)}]]
                 [:div {:class "flex justify-center mt-6 space-x-4"}
                  [:a {:href  "/private/auth/list-roles"
                       :class (sc :cancel-button)}
                   "Back"]
                  [:button {:type  "submit"
                            :class (sc :button-new)}
                   "Create"]])]]
    :footer-content (core/footer)}))

(defn show
  [role]
  (layout
   {:title          "Show Role"
    :header-content (core/header)
    :main-content
    [:div {:class "flex justify-center"}
     [:div {:class "bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"}
      [:h1 {:class "text-2xl font-bold mb-6 text-center"} "Role Details"]
      [:div {:class "space-y-4"}
       [:div
        [:label {:class (sc :form-label)} "Name"]
        [:p {:class "mt-1 text-gray-900"} (:name role)]]
       [:div
        [:label {:class (sc :form-label)} "Description"]
        [:p {:class "mt-1 text-gray-900"} (:description role)]]]
      [:div {:class "flex justify-center mt-6 space-x-4"}
       [:a {:href "/private/auth/list-roles" :class (sc :cancel-button)} "Back"]
       [:a {:href (str "/private/auth/list-role-permissions?id=" (:id role))
            :class (sc :button-new)} "Permissions"]]]]
    :footer-content (core/footer)}))

(defn edit
  [role]
  (layout
   {:title          "Edit Role"
    :header-content (core/header)
    :main-content
    [:div {:class "flex justify-center"}
     [:div {:class "bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"}
      [:h1 {:class "text-2xl font-bold mb-6 text-center"} "Edit Role"]
      (core/form {:action (str "/private/auth/update-role?id=" (:id role))
                  :method "POST"
                  :class  "space-y-6"}
                 [:input {:type "hidden" :name "id" :value (:id role)}]
                 [:div
                  [:label {:for   "name" :class (sc :form-label)} "Name"]
                  [:input {:type     "text"
                           :name     "name"
                           :id       "name"
                           :value    (:name role)
                           :class    (sc :form-input)}]]
                 [:div
                  [:label {:for   "description" :class (sc :form-label)} "Description"]
                  [:textarea {:name  "description"
                              :id    "description"
                              :class (sc :form-input)}
                   (:description role)]]
                 [:div {:class "flex justify-center mt-6 space-x-4"}
                  [:a {:href  "/private/auth/list-roles"
                       :class (sc :cancel-button)}
                   "Cancel"]
                  [:button {:type  "submit"
                            :class (sc :button-new)}
                   "Update"]])]]
    :footer-content (core/footer)}))

(defn list-user-roles
  [user-id roles]
  (layout
   {:title          "Roles Management for User"
    :header-content (core/header)
    :main-content
    [:div {:class "space-y-8"}
     [:h1 {:class "text-2xl font-bold mb-4"} "Roles Management for User"]

     ;; Table for assigned roles
     [:h2 {:class "text-xl font-bold mb-2"} "Assigned Roles"]
     [:table {:class (sc :table)}
      [:thead {:class (sc :thead)}
       [:tr
        [:th {:class (sc :th)} "Name"]
        [:th {:class (sc :th)} "Description"]
        [:th {:class (sc :th-actions)} "Action"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [role (filter #(= 1 (:is_assigned %)) roles)]
         [:tr {:key (:role_id role)}
          [:td {:class (sc :td-primary)} (:role_name role)]
          [:td {:class (sc :td-secondary)} (:role_description role)]
          [:td {:class (sc :td-actions)}
           (core/form {:action "/private/auth/unassign-role-from-user" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "user-id" :value user-id}]
                      [:input {:type "hidden" :name "role-id" :value (:role_id role)}]
                      [:button {:type "submit" :class (sc :button-delete)} "Unassign"])]])]]

     ;; Table for unassigned roles
     [:h2 {:class "text-xl font-bold mb-2"} "Unassigned Roles"]
     [:table {:class (sc :table)}
      [:thead {:class (sc :thead)}
       [:tr
        [:th {:class (sc :th)} "Name"]
        [:th {:class (sc :th)} "Description"]
        [:th {:class (sc :th-actions)} "Action"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [role (filter #(= 0 (:is_assigned %)) roles)]
         [:tr {:key (:role_id role)}
          [:td {:class (sc :td-primary)} (:role_name role)]
          [:td {:class (sc :td-secondary)} (:role_description role)]
          [:td {:class (sc :td-actions)}
           (core/form {:action "/private/auth/assign-role-to-user" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "user-id" :value user-id}]
                      [:input {:type "hidden" :name "role-id" :value (:role_id role)}]
                      [:button {:type "submit" :class (sc :button-new)} "Assign"])]])]]]
    :footer-content (core/footer)}))