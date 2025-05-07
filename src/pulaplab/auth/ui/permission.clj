(ns pulaplab.auth.ui.permission
  (:require [pulaplab.ui.layout :refer [layout]]
            [pulaplab.ui.styles :as styles]
            [pulaplab.auth.ui.core :as core]))

(defn index
  [permissions flash]
  (layout
   {:title          "Permission List"
    :header-content (core/header)
    :main-content
    [:div {:class "space-y-8"}
     (when flash
       [:div {:class "bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded"}
        flash])
     [:h1 {:class "text-2xl font-bold mb-4"} "Permission List"]
     [:table {:class (styles/get-class :table)}
      [:thead {:class (styles/get-class :thead)}
       [:tr
        [:th {:class (styles/get-class :th)} "Name"]
        [:th {:class (styles/get-class :th)} "Description"]
        [:th {:class (styles/get-class :th-actions)} "Actions"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [{:keys [id name description]} permissions]
         [:tr {:key id}
          [:td {:class (styles/get-class :td-primary)}
           [:a {:href  (str "/private/auth/show-permission?id=" id)
                :class (styles/get-class :link-primary)}
            name]]
          [:td {:class (styles/get-class :td-secondary)} description]
          [:td {:class (styles/get-class :td-actions)}
           [:a {:href  (str "/private/auth/show-permission?id=" id)
                :class (styles/get-class :button-show)} "Show"]
           [:a {:href  (str "/private/auth/edit-permission?id=" id)
                :class (styles/get-class :button-edit)} "Edit"]
           (core/form {:action "/private/auth/delete-permission" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "id" :value id}]
                      [:button {:type  "submit" :class (styles/get-class :button-delete)}
                       "Delete"])]])]]
     [:div {:class "flex justify-center mt-6"}
      [:a {:href "/private/auth/new-permission"
           :class (styles/get-class :button-new)}
       "New"]]]
    :footer-content (core/footer)}))

(defn new
  []
  (layout
   {:title          "New Permission"
    :header-content (core/header)
    :main-content
    [:div {:class "flex justify-center"}
     [:div {:class "bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"}
      [:h1 {:class "text-2xl font-bold mb-6 text-center"} "Create New Permission"]
      (core/form {:action "/private/auth/create-permission"
                  :method "POST"
                  :class  "space-y-6"}
                 [:input {:type  "hidden" :name "id" :value ""}]
                 [:div
                  [:label {:for   "name" :class (styles/get-class :form-label)} "Name"]
                  [:input {:type     "text"
                           :name     "name"
                           :id       "name"
                           :required true
                           :class    (styles/get-class :form-input)}]]
                 [:div
                  [:label {:for   "description" :class (styles/get-class :form-label)} "Description"]
                  [:textarea {:name  "description"
                              :id    "description"
                              :class (styles/get-class :form-input)}]]
                 [:div {:class "flex justify-center mt-6 space-x-4"}
                  [:a {:href  "/private/auth/list-permissions"
                       :class (styles/get-class :cancel-button)}
                   "Back"]
                  [:button {:type  "submit"
                            :class (styles/get-class :button-new)}
                   "Create"]])]]
    :footer-content (core/footer)}))

(defn show
  [permission]
  (layout
   {:title          "Show Permission"
    :header-content (core/header)
    :main-content
    [:div {:class "flex justify-center"}
     [:div {:class "bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"}
      [:h1 {:class "text-2xl font-bold mb-6 text-center"} "Permission Details"]
      [:div {:class "space-y-4"}
       [:div
        [:label {:class (styles/get-class :form-label)} "Name"]
        [:p {:class "mt-1 text-gray-900"} (:name permission)]]
       [:div
        [:label {:class (styles/get-class :form-label)} "Description"]
        [:p {:class "mt-1 text-gray-900"} (:description permission)]]]
      [:div {:class "flex justify-center mt-6 space-x-4"}
       [:a {:href  "/private/auth/list-permissions"
            :class (styles/get-class :cancel-button)}
        "Back"]]]]
    :footer-content (core/footer)}))

(defn edit
  [permission]
  (layout
   {:title          "Edit Permission"
    :header-content (core/header)
    :main-content
    [:div {:class "flex justify-center"}
     [:div {:class "bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"}
      [:h1 {:class "text-2xl font-bold mb-6 text-center"} "Edit Permission"]
      (core/form {:action (str "/private/auth/update-permission?id=" (:id permission))
                  :method "POST"
                  :class  "space-y-6"}
                 [:input {:type "hidden" :name "id" :value (:id permission)}]
                 [:div
                  [:label {:for   "name" :class (styles/get-class :form-label)} "Name"]
                  [:input {:type     "text"
                           :name     "name"
                           :id       "name"
                           :value    (:name permission)
                           :class    (styles/get-class :form-input)}]]
                 [:div
                  [:label {:for   "description" :class (styles/get-class :form-label)} "Description"]
                  [:textarea {:name  "description"
                              :id    "description"
                              :class (styles/get-class :form-input)} (:description permission)]]
                 [:div {:class "flex justify-center mt-6 space-x-4"}
                  [:a {:href  "/private/auth/list-permissions"
                       :class (styles/get-class :cancel-button)}
                   "Cancel"]
                  [:button {:type  "submit"
                            :class (styles/get-class :button-new)}
                   "Update"]])]]
    :footer-content (core/footer)}))

(defn list-user-permissions
  [user-id permissions]
  (layout
   {:title          "Permissions Management for User"
    :header-content (core/header)
    :main-content
    [:div {:class "space-y-8"}
     [:h1 {:class "text-2xl font-bold mb-4"} "Permissions Management for User"]

     ;; Table for assigned permissions
     [:h2 {:class "text-xl font-bold mb-2"} "Assigned Permissions"]
     [:table {:class (styles/get-class :table)}
      [:thead {:class (styles/get-class :thead)}
       [:tr
        [:th {:class (styles/get-class :th)} "Name"]
        [:th {:class (styles/get-class :th)} "Source"]
        [:th {:class (styles/get-class :th-actions)} "Action"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [permission (filter #(and (not= "Unassigned" (:source %)) (:source %)) permissions)]
         [:tr {:key (:permission_id permission)
               :class (when (not= "Direct" (:source permission)) "shadow-sm")}
          [:td {:class (styles/get-class :td-primary)} (:permission_name permission)]
          [:td {:class (styles/get-class :td-secondary)} (:source permission)]
          [:td {:class (styles/get-class :td-actions)}
           (core/form {:action "/private/auth/unassign-permission-from-user" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "user-id" :value user-id}]
                      [:input {:type "hidden" :name "permission-id" :value (:permission_id permission)}]
                      [:button {:type "submit" :class (styles/get-class :button-delete)} "Unassign"])]])]]

     ;; Table for unassigned permissions
     [:h2 {:class "text-xl font-bold mb-2"} "Unassigned Permissions"]
     [:table {:class (styles/get-class :table)}
      [:thead {:class (styles/get-class :thead)}
       [:tr
        [:th {:class (styles/get-class :th)} "Name"]
        [:th {:class (styles/get-class :th)} "Source"] ;; Added to align colums
        [:th {:class (styles/get-class :th-actions)} "Action"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [permission (filter #(= "Unassigned" (:source %)) permissions)]
         [:tr {:key (:permission_id permission)}
          [:td {:class (styles/get-class :td-primary)} (:permission_name permission)]
          [:td {:class (styles/get-class :td-secondary)} ""] ;; Empty cell for alignment
          [:td {:class (styles/get-class :td-actions)}
           (core/form {:action "/private/auth/assign-permission-to-user" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "user-id" :value user-id}]
                      [:input {:type "hidden" :name "permission-id" :value (:permission_id permission)}]
                      [:button {:type "submit" :class (styles/get-class :button-new)} "Assign"])]])]]]
    :footer-content (core/footer)}))

(defn list-role-permissions
  [role-id permissions]
  (layout
   {:title          "Permissions Management for Role"
    :header-content (core/header)
    :main-content
    [:div {:class "space-y-8"}
     [:h1 {:class "text-2xl font-bold mb-4"} "Permissions Management for Role"]

     ;; Table for assigned permissions
     [:h2 {:class "text-xl font-bold mb-2"} "Assigned Permissions"]
     [:table {:class (styles/get-class :table)}
      [:thead {:class (styles/get-class :thead)}
       [:tr
        [:th {:class (styles/get-class :th)} "Name"]
        [:th {:class (styles/get-class :th)} "Source"]
        [:th {:class (styles/get-class :th-actions)} "Action"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [permission (filter #(and (not= "Unassigned" (:source %)) (:source %)) permissions)]
         [:tr {:key (:permission_id permission)
               :class (when (not= "Direct" (:source permission)) "shadow-sm")}
          [:td {:class (styles/get-class :td-primary)} (:permission_name permission)]
          [:td {:class (styles/get-class :td-secondary)} (:source permission)]
          [:td {:class (styles/get-class :td-actions)}
           (core/form {:action "/private/auth/unassign-permission-from-role" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "role-id" :value role-id}]
                      [:input {:type "hidden" :name "permission-id" :value (:permission_id permission)}]
                      [:button {:type  "submit" :class (styles/get-class :button-delete)}
                       "Unassign"])]])]]

     ;; Table for unassigned permissions
     [:h2 {:class "text-xl font-bold mb-2"} "Unassigned Permissions"]
     [:table {:class (styles/get-class :table)}
      [:thead {:class (styles/get-class :thead)}
       [:tr
        [:th {:class (styles/get-class :th)} "Name"]
        [:th {:class (styles/get-class :th)} "Source"]
        [:th {:class (styles/get-class :th-actions)} "Action"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [permission (filter #(= "Unassigned" (:source %)) permissions)]
         [:tr {:key (:permission_id permission)}
          [:td {:class (styles/get-class :td-primary)} (:permission_name permission)]
          [:td {:class (styles/get-class :td-secondary)} ""] ;; Empty cell for alignment
          [:td {:class (styles/get-class :td-actions)}
           (core/form {:action "/private/auth/assign-permission-to-role" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "role-id" :value role-id}]
                      [:input {:type "hidden" :name "permission-id" :value (:permission_id permission)}]
                      [:button {:type  "submit" :class (styles/get-class :button-new)}
                       "Assign"])]])]]]
    :footer-content (core/footer)}))

(defn list-permission-roles
  [permission-id roles]
  (layout
   {:title          "Roles Management for Permission"
    :header-content (core/header)
    :main-content
    [:div {:class "space-y-8"}
     [:h1 {:class "text-2xl font-bold mb-4"} "Roles with this Permission"]

     ;; Table for assigned roles
     [:h2 {:class "text-xl font-bold mb-2"} "Assigned Roles"]
     [:table {:class (styles/get-class :table)}
      [:thead {:class (styles/get-class :thead)}
       [:tr
        [:th {:class (styles/get-class :th)} "Name"]
        [:th {:class (styles/get-class :th-actions)} "Action"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [role (filter #(= "Assigned" (:status %)) roles)]
         [:tr {:key (:role_id role)}
          [:td {:class (styles/get-class :td-primary)} (:role_name role)]
          [:td {:class (styles/get-class :td-actions)}
           (core/form {:action "/private/auth/unassign-permission-from-role" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "permission-id" :value permission-id}]
                      [:input {:type "hidden" :name "role-id" :value (:role_id role)}]
                      [:button {:type "submit" :class (styles/get-class :button-delete)} "Unassign"])]])]]

     ;; Table for unassigned roles
     [:h2 {:class "text-xl font-bold mb-2"} "Unassigned Roles"]
     [:table {:class (styles/get-class :table)}
      [:thead {:class (styles/get-class :thead)}
       [:tr
        [:th {:class (styles/get-class :th)} "Name"]
        [:th {:class (styles/get-class :th-actions)} "Action"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [role (filter #(= "Unassigned" (:status %)) roles)]
         [:tr {:key (:role_id role)}
          [:td {:class (styles/get-class :td-primary)} (:role_name role)]
          [:td {:class (styles/get-class :td-actions)}
           (core/form {:action "/private/auth/assign-permission-to-role" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "permission-id" :value permission-id}]
                      [:input {:type "hidden" :name "role-id" :value (:role_id role)}]
                      [:button {:type "submit" :class (styles/get-class :button-new)} "Assign"])]])]]]
    :footer-content (core/footer)}))

(defn list-resource-permissions
  [resource-id permissions]
  (layout
   {:title          "Permissions Management for Resource"
    :header-content (core/header)
    :main-content
    [:div {:class "space-y-8"}
     [:h1 {:class "text-2xl font-bold mb-4"} "Permissions Management for Resource"]

     ;; Table for assigned permissions
     [:h2 {:class "text-xl font-bold mb-2"} "Assigned Permissions"]
     [:table {:class (styles/get-class :table)}
      [:thead {:class (styles/get-class :thead)}
       [:tr
        [:th {:class (styles/get-class :th)} "Name"]
        [:th {:class (styles/get-class :th)} "Source"]
        [:th {:class (styles/get-class :th-actions)} "Action"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [permission (filter #(and (not= "Unassigned" (:source %)) (:source %)) permissions)]
         [:tr {:key (:permission_id permission)
               :class (when (not= "Direct" (:source permission)) "shadow-sm")}
          [:td {:class (styles/get-class :td-primary)} (:permission_name permission)]
          [:td {:class (styles/get-class :td-secondary)} (:source permission)]
          [:td {:class (styles/get-class :td-actions)}
           (core/form {:action "/private/auth/unassign-permission-from-resource" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "resource-id" :value resource-id}]
                      [:input {:type "hidden" :name "permission-id" :value (:permission_id permission)}]
                      [:button {:type "submit" :class (styles/get-class :button-delete)} "Unassign"])]])]]

     ;; Table for unassigned permissions
     [:h2 {:class "text-xl font-bold mb-2"} "Unassigned Permissions"]
     [:table {:class (styles/get-class :table)} [:thead {:class (styles/get-class :thead)}
                                                 [:tr
                                                  [:th {:class (styles/get-class :th)} "Name"]
                                                  [:th {:class (styles/get-class :th)} "Source"]
                                                  [:th {:class (styles/get-class :th-actions)} "Action"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [permission (filter #(= "Unassigned" (:source %)) permissions)]
         [:tr {:key (:permission_id permission)}
          [:td {:class (styles/get-class :td-primary)} (:permission_name permission)]
          [:td {:class (styles/get-class :td-secondary)} ""] ;; Empty cell for alignment
          [:td {:class (styles/get-class :td-actions)}
           (core/form {:action "/private/auth/assign-permission-to-resource" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "resource-id" :value resource-id}]
                      [:input {:type "hidden" :name "permission-id" :value (:permission_id permission)}]
                      [:button {:type "submit" :class (styles/get-class :button-new)} "Assign"])]])]]]
    :footer-content (core/footer)}))
