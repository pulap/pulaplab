(ns pulaplab.auth.ui.resource
  (:require [pulaplab.ui.layout :refer [layout]]
            [pulaplab.ui.styles :as styles]
            [pulaplab.auth.ui.core :as core]))

;; Use the predefined `resource-views` function from `pulaplab.ui.styles`
(def sc (styles/resource-views))

(defn index
  [resources flash]
  (layout
   {:title          "Resource List"
    :header-content (core/header)
    :main-content
    [:div {:class "space-y-8"}
     (when flash
       [:div {:class "bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded"}
        flash])
     [:h1 {:class "text-2xl font-bold mb-4"} "Resource List"]
     [:table {:class (sc :table)}
      [:thead {:class (sc :thead)}
       [:tr
        [:th {:class (sc :th)} "Name"]
        [:th {:class (sc :th)} "Description"]
        [:th {:class (sc :th-actions)} "Actions"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [{:keys [id name description]} resources]
         [:tr {:key id}
          [:td {:class (sc :td-primary)}
           [:a {:href  (str "/private/auth/show-resource?id=" id)
                :class (sc :link-primary)}
            name]]
          [:td {:class (sc :td-secondary)} description]
          [:td {:class (sc :td-actions)}
           [:a {:href  (str "/private/auth/show-resource?id=" id)
                :class (sc :button-show)} "Show"]
           [:a {:href  (str "/private/auth/edit-resource?id=" id)
                :class (sc :button-edit)} "Edit"]
           (core/form {:action "/private/auth/delete-resource" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "id" :value id}]
                      [:button {:type  "submit" :class (sc :button-delete)}
                       "Delete"])]])]]
     [:div {:class "flex justify-center mt-6"}
      [:a {:href "/private/auth/new-resource"
           :class (sc :button-new)}
       "New"]]]
    :footer-content (core/footer)}))

(defn new
  []
  (layout
   {:title          "New Resource"
    :header-content (core/header)
    :main-content
    [:div {:class "flex justify-center"}
     [:div {:class "bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"}
      [:h1 {:class "text-2xl font-bold mb-6 text-center"} "Create New Resource"]
      (core/form {:action "/private/auth/create-resource"
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
                  [:a {:href  "/private/auth/list-resources"
                       :class (sc :cancel-button)}
                   "Back"]
                  [:button {:type  "submit"
                            :class (sc :button-new)}
                   "Create"]])]]
    :footer-content (core/footer)}))

(defn show
  [resource]
  (layout
   {:title          "Show Resource"
    :header-content (core/header)
    :main-content
    [:div {:class "flex justify-center"}
     [:div {:class "bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"}
      [:h1 {:class "text-2xl font-bold mb-6 text-center"} "Resource Details"]
      [:div {:class "space-y-4"}
       [:div
        [:label {:class (sc :form-label)} "Name"]
        [:p {:class "mt-1 text-gray-900"} (:name resource)]]
       [:div
        [:label {:class (sc :form-label)} "Description"]
        [:p {:class "mt-1 text-gray-900"} (:description resource)]]]
      [:div {:class "flex justify-center mt-6 space-x-4"}
       [:a {:href  "/private/auth/list-resources"
            :class (sc :cancel-button)}
        "Back"]
       [:a {:href  (str "/private/auth/list-resource-permissions?id=" (:id resource))
            :class (sc :button-new)}
        "Permissions"]]]]
    :footer-content (core/footer)}))

(defn edit
  [resource]
  (layout
   {:title          "Edit Resource"
    :header-content (core/header)
    :main-content
    [:div {:class "flex justify-center"}
     [:div {:class "bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 w-full max-w-md"}
      [:h1 {:class "text-2xl font-bold mb-6 text-center"} "Edit Resource"]
      (core/form {:action (str "/private/auth/update-resource?id=" (:id resource))
                  :method "POST"
                  :class  "space-y-6"}
                 [:input {:type "hidden" :name "id" :value (:id resource)}]
                 [:div
                  [:label {:for   "name" :class (sc :form-label)} "Name"]
                  [:input {:type     "text"
                           :name     "name"
                           :id       "name"
                           :value    (:name resource)
                           :class    (sc :form-input)}]]
                 [:div
                  [:label {:for   "description" :class (sc :form-label)} "Description"]
                  [:textarea {:name  "description"
                              :id    "description"
                              :class (sc :form-input)}
                   (:description resource)]]
                 [:div {:class "flex justify-center mt-6 space-x-4"}
                  [:a {:href  "/private/auth/list-resources"
                       :class (sc :cancel-button)}
                   "Cancel"]
                  [:button {:type  "submit"
                            :class (sc :button-new)}
                   "Update"]])]]
    :footer-content (core/footer)}))
