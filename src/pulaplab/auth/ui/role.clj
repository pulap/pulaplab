(ns pulaplab.auth.ui.role
  (:require [pulaplab.ui.layout :refer [layout]]
            [pulaplab.ui.styles :as styles]
            [pulaplab.auth.ui.core :as core]))

(defn list-roles-page
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
     [:table {:class (styles/get-class :table)}
      [:thead {:class (styles/get-class :thead)}
       [:tr
        [:th {:class (styles/get-class :th)} "Name"]
        [:th {:class (styles/get-class :th)} "Description"]
        [:th {:class (styles/get-class :th)} "Actions"]]]
      [:tbody {:class "bg-white divide-y divide-gray-200"}
       (for [{:keys [id name slug]} roles]
         [:tr {:key id}
          [:td {:class (styles/get-class :td-name)}
           [:a {:href  (str "/private/auth/show-role?id=" id)
                :class (styles/get-class :link-name)}
            name]]
          [:td {:class (styles/get-class :td-slug)} slug]
          [:td {:class (styles/get-class :td-actions)}
           [:a {:href  (str "/private/auth/show-role?id=" id)
                :class (styles/get-class :button-show)} "Show"]
           [:a {:href  (str "/private/auth/edit-role?id=" id)
                :class (styles/get-class :button-edit)} "Edit"]
           (core/form {:action "/private/auth/delete-role" :method "POST" :class "inline"}
                      [:input {:type "hidden" :name "id" :value id}]
                      [:button {:type  "submit" :class (styles/get-class :button-delete)}
                       "Delete"])]])]]
     [:div {:class "flex justify-center mt-6"}
      [:a {:href  "/private/auth/new-role"
           :class (styles/get-class :button-new)}
       "New"]]]
    :footer-content (core/footer)}))

(defn new-role-page
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
                  [:a {:href  "/private/auth/list-roles"
                       :class (styles/get-class :cancel-button)}
                   "Back"]
                  [:button {:type  "submit"
                            :class (styles/get-class :button-new)}
                   "Create"]])]]
    :footer-content (core/footer)}))

(defn show-role-page
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
        [:label {:class (styles/get-class :form-label)} "Name"]
        [:p {:class "mt-1 text-gray-900"} (:name role)]]
       [:div
        [:label {:class (styles/get-class :form-label)} "Description"]
        [:p {:class "mt-1 text-gray-900"} (:description role)]]]
      [:div {:class "flex justify-center mt-6"}
       [:a {:href  "/private/auth/list-roles"
            :class (styles/get-class :cancel-button)}
        "Back"]]]]
    :footer-content (core/footer)}))

(defn edit-role-page
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
                 [:div
                  [:label {:for   "name" :class (styles/get-class :form-label)} "Name"]
                  [:input {:type     "text"
                           :name     "name"
                           :id       "name"
                           :value    (:name role)
                           :class    (styles/get-class :form-input)}]]
                 [:div
                  [:label {:for   "description" :class (styles/get-class :form-label)} "Description"]
                  [:textarea {:name  "description"
                              :id    "description"
                              :class (styles/get-class :form-input)}]
                  (:description role)]
                 [:div {:class "flex justify-center mt-6 space-x-4"}
                  [:a {:href  "/private/auth/list-roles"
                       :class (styles/get-class :cancel-button)}
                   "Cancel"]
                  [:button {:type  "submit"
                            :class (styles/get-class :button-new)}
                   "Update"]])]]
    :footer-content (core/footer)}))