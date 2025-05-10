(ns pulaplab.auth.ui.permission
  (:require [pulaplab.ui.layout :refer [layout]]
            [pulaplab.ui.styles :as styles]
            [pulaplab.auth.ui.core :as core]
            [clojure.string :as str]))

;; Updated to use the unified `views` function
(def sc (styles/views))

(def base-url "/private/auth/")

(defn url [command & [params]]
  (str base-url command
       (when params
         (str "?" (str/join "&" (map (fn [[k v]] (str (name k) "=" v)) params))))))

;; Example usage:
;; (url "show-permission" {:id 123}) => "/private/auth/show-permission?id=123"
;; (url "list-permissions") => "/private/auth/list-permissions"

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
     [:table {:class (sc :table)}
      [:thead {:class (sc :thead)}
       [:tr
        [:th {:class (sc :th)} "Name"]
        [:th {:class (sc :th)} "Description"]
        [:th {:class (sc :th-actions)} "Actions"]]]
      [:tbody {:class (sc :tbody)}
       (for [{:keys [id name description]} permissions]
         [:tr {:key id}
          [:td {:class (sc :td-primary)}
           [:a {:href  (url "show-permission" {:id id})
                :class (sc :link-primary)}
            name]]
          [:td {:class (sc :td-secondary)} description]
          [:td {:class (sc :td-actions)}
           [:a {:href  (url "show-permission" {:id id})
                :class (sc :button-show)} "Show"]
           [:a {:href  (url "edit-permission" {:id id})
                :class (sc :button-edit)} "Edit"]
           (core/form {:action (url "delete-permission") :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "id" :value id}]
                      [:button {:type  "submit" :class (sc :button-delete)}
                       "Delete"])]])]]
     [:div {:class "flex justify-center mt-6"}
      [:a {:href (url "new-permission")
           :class (sc :button-new)}
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
      (core/form {:action (url "create-permission")
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
                  [:a {:href  (url "list-permissions")
                       :class (sc :cancel-button)}
                   "Back"]
                  [:button {:type  "submit"
                            :class (sc :button-new)}
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
        [:label {:class (sc :form-label)} "Name"]
        [:p {:class "mt-1 text-gray-900"} (:name permission)]]
       [:div
        [:label {:class (sc :form-label)} "Description"]
        [:p {:class "mt-1 text-gray-900"} (:description permission)]]]
      [:div {:class "flex justify-center mt-6 space-x-4"}
       [:a {:href  (url "list-permissions")
            :class (sc :cancel-button)}
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
      (core/form {:action (url "update-permission" {:id (:id permission)})
                  :method "POST"
                  :class  "space-y-6"}
                 [:input {:type "hidden" :name "id" :value (:id permission)}]
                 [:div
                  [:label {:for   "name" :class (sc :form-label)} "Name"]
                  [:input {:type     "text"
                           :name     "name"
                           :id       "name"
                           :value    (:name permission)
                           :class    (sc :form-input)}]]
                 [:div
                  [:label {:for   "description" :class (sc :form-label)} "Description"]
                  [:textarea {:name  "description"
                              :id    "description"
                              :class (sc :form-input)} (:description permission)]]
                 [:div {:class "flex justify-center mt-6 space-x-4"}
                  [:a {:href  (url "list-permissions")
                       :class (sc :cancel-button)}
                   "Cancel"]
                  [:button {:type  "submit"
                            :class (sc :button-new)}
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
     [:table {:class (sc :table)}
      [:thead {:class (sc :thead)}
       [:tr
        [:th {:class (sc :th)} "Name"]
        [:th {:class (sc :th)} "Source"]
        [:th {:class (sc :th-actions)} "Action"]]]
      [:tbody {:class (sc :tbody)}
       (for [permission (filter #(and (not= "Unassigned" (:source %)) (:source %)) permissions)]
         [:tr {:key (:permission_id permission)
               :class (when (not= "Direct" (:source permission)) "shadow-sm")}
          [:td {:class (sc :td-primary)} (:permission_name permission)]
          [:td {:class (sc :td-secondary)} (:source permission)]
          [:td {:class (sc :td-actions)}
           (core/form {:action (url "unassign-permission-from-user") :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "user-id" :value user-id}]
                      [:input {:type "hidden" :name "permission-id" :value (:permission_id permission)}]
                      [:button {:type "submit" :class (sc :button-delete)} "Unassign"])]])]]

     ;; Table for unassigned permissions
     [:h2 {:class "text-xl font-bold mb-2"} "Unassigned Permissions"]
     [:table {:class (sc :table)}
      [:thead {:class (sc :thead)}
       [:tr
        [:th {:class (sc :th)} "Name"]
        [:th {:class (sc :th)} "Source"] ;; Added to align colums
        [:th {:class (sc :th-actions)} "Action"]]]
      [:tbody {:class (sc :tbody)}
       (for [permission (filter #(= "Unassigned" (:source %)) permissions)]
         [:tr {:key (:permission_id permission)}
          [:td {:class (sc :td-primary)} (:permission_name permission)]
          [:td {:class (sc :td-secondary)} ""] ;; Empty cell for alignment
          [:td {:class (sc :td-actions)}
           (core/form {:action (url "assign-permission-to-user") :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "user-id" :value user-id}]
                      [:input {:type "hidden" :name "permission-id" :value (:permission_id permission)}]
                      [:button {:type "submit" :class (sc :button-new)} "Assign"])]])]]]
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
     [:table {:class (sc :table)}
      [:thead {:class (sc :thead)}
       [:tr
        [:th {:class (sc :th)} "Name"]
        [:th {:class (sc :th)} "Source"]
        [:th {:class (sc :th-actions)} "Action"]]]
      [:tbody {:class (sc :tbody)}
       (for [permission (filter #(and (not= "Unassigned" (:source %)) (:source %)) permissions)]
         [:tr {:key (:permission_id permission)
               :class (when (not= "Direct" (:source permission)) "shadow-sm")}
          [:td {:class (sc :td-primary)} (:permission_name permission)]
          [:td {:class (sc :td-secondary)} (:source permission)]
          [:td {:class (sc :td-actions)}
           (core/form {:action (url "unassign-permission-from-role") :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "role-id" :value role-id}]
                      [:input {:type "hidden" :name "permission-id" :value (:permission_id permission)}]
                      [:button {:type  "submit" :class (sc :button-delete)}
                       "Unassign"])]])]]

     ;; Table for unassigned permissions
     [:h2 {:class "text-xl font-bold mb-2"} "Unassigned Permissions"]
     [:table {:class (sc :table)}
      [:thead {:class (sc :thead)}
       [:tr
        [:th {:class (sc :th)} "Name"]
        [:th {:class (sc :th)} "Source"]
        [:th {:class (sc :th-actions)} "Action"]]]
      [:tbody {:class (sc :tbody)}
       (for [permission (filter #(= "Unassigned" (:source %)) permissions)]
         [:tr {:key (:permission_id permission)}
          [:td {:class (sc :td-primary)} (:permission_name permission)]
          [:td {:class (sc :td-secondary)} ""] ;; Empty cell for alignment
          [:td {:class (sc :td-actions)}
           (core/form {:action (url "assign-permission-to-role") :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "role-id" :value role-id}]
                      [:input {:type "hidden" :name "permission-id" :value (:permission_id permission)}]
                      [:button {:type  "submit" :class (sc :button-new)}
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
     [:table {:class (sc :table)}
      [:thead {:class (sc :thead)}
       [:tr
        [:th {:class (sc :th)} "Name"]
        [:th {:class (sc :th-actions)} "Action"]]]
      [:tbody {:class (sc :tbody)}
       (for [role (filter #(= "Assigned" (:status %)) roles)]
         [:tr {:key (:role_id role)}
          [:td {:class (sc :td-primary)} (:role_name role)]
          [:td {:class (sc :td-actions)}
           (core/form {:action (url "unassign-permission-from-role") :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "permission-id" :value permission-id}]
                      [:input {:type "hidden" :name "role-id" :value (:role_id role)}]
                      [:button {:type "submit" :class (sc :button-delete)} "Unassign"])]])]]

     ;; Table for unassigned roles
     [:h2 {:class "text-xl font-bold mb-2"} "Unassigned Roles"]
     [:table {:class (sc :table)}
      [:thead {:class (sc :thead)}
       [:tr
        [:th {:class (sc :th)} "Name"]
        [:th {:class (sc :th-actions)} "Action"]]]
      [:tbody {:class (sc :tbody)}
       (for [role (filter #(= "Unassigned" (:status %)) roles)]
         [:tr {:key (:role_id role)}
          [:td {:class (sc :td-primary)} (:role_name role)]
          [:td {:class (sc :td-actions)}
           (core/form {:action (url "assign-permission-to-role") :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "permission-id" :value permission-id}]
                      [:input {:type "hidden" :name "role-id" :value (:role_id role)}]
                      [:button {:type "submit" :class (sc :button-new)} "Assign"])]])]]]
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
     [:table {:class (sc :table)}
      [:thead {:class (sc :thead)}
       [:tr
        [:th {:class (sc :th)} "Name"]
        [:th {:class (sc :th)} "Source"]
        [:th {:class (sc :th-actions)} "Action"]]]
      [:tbody {:class (sc :tbody)}
       (for [permission (filter #(and (not= "Unassigned" (:source %)) (:source %)) permissions)]
         [:tr {:key (:permission_id permission)
               :class (when (not= "Direct" (:source permission)) "shadow-sm")}
          [:td {:class (sc :td-primary)} (:permission_name permission)]
          [:td {:class (sc :td-secondary)} (:source permission)]
          [:td {:class (sc :td-actions)}
           (core/form {:action (url "unassign-permission-from-resource") :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "resource-id" :value resource-id}]
                      [:input {:type "hidden" :name "permission-id" :value (:permission_id permission)}]
                      [:button {:type "submit" :class (sc :button-delete)} "Unassign"])]])]]

     ;; Table for unassigned permissions
     [:h2 {:class "text-xl font-bold mb-2"} "Unassigned Permissions"]
     [:table {:class (sc :table)} [:thead {:class (sc :thead)}
                                   [:tr
                                    [:th {:class (sc :th)} "Name"]
                                    [:th {:class (sc :th)} "Source"]
                                    [:th {:class (sc :th-actions)} "Action"]]]
      [:tbody {:class (sc :tbody)}
       (for [permission (filter #(= "Unassigned" (:source %)) permissions)]
         [:tr {:key (:permission_id permission)}
          [:td {:class (sc :td-primary)} (:permission_name permission)]
          [:td {:class (sc :td-secondary)} ""] ;; Empty cell for alignment
          [:td {:class (sc :td-actions)}
           (core/form {:action (url "assign-permission-to-resource") :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "resource-id" :value resource-id}]
                      [:input {:type "hidden" :name "permission-id" :value (:permission_id permission)}]
                      [:button {:type "submit" :class (sc :button-new)} "Assign"])]])]]]
    :footer-content (core/footer)}))
