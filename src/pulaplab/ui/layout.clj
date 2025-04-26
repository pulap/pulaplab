(ns pulaplab.ui.layout
  (:require [hiccup.page :refer [html5]]))

(defn layout [{:keys [title header-content main-content aside-content footer-content]}]
  (html5
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:title title]
    [:script {:src "https://cdn.tailwindcss.com"}]]
   [:body {:class "bg-gray-100 text-gray-900 min-h-screen flex flex-col"}
    [:header {:class "bg-blue-600 text-white p-4 flex items-center"}
     [:div {:class "w-12 h-12 bg-gray-300 mr-4"}] ;; Placeholder para logo
     [:nav {:class "flex-1"}
      header-content]]

    [:main {:class "flex-1 p-4"}
     main-content]

    (when aside-content
      [:aside {:class "p-4"}
       aside-content])

    [:footer {:class "bg-gray-200 text-center p-4 mt-4"}
     footer-content]]))
