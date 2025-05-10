(ns pulaplab.ui.styles)

(def classes
  {:table "min-w-full divide-y divide-gray-200"
   :thead "bg-gray-50"
   :th "px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/3 bg-white"
   :th-actions "px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider w-1/3"
   :td-username "px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900"
   :td-primary "px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 bg-white border-b border-gray-200"
   :td-email "px-6 py-4 whitespace-nowrap text-sm text-gray-500"
   :td-secondary "px-6 py-4 whitespace-nowrap text-sm text-gray-500 bg-white border-b border-gray-200"
   :td-actions "px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-center space-x-2 flex justify-center bg-white border-b border-gray-200"
   :link-username "text-blue-500 hover:underline"
   :link-primary "text-blue-500 hover:underline"
   :button-show "inline-block bg-green-500 text-white px-6 py-2 rounded w-24"
   :button-edit "inline-block bg-yellow-500 text-white px-6 py-2 rounded w-24"
   :button-delete "inline-flex bg-red-500 text-white px-4 py-2 rounded justify-center items-center"
   :button-new "bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
   :form-label "block text-sm font-medium text-gray-700"
   :form-input "mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm px-3 py-2"
   :header-link "text-white"
   :menu-list "flex space-x-4"
   :cancel-button "bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded"
   :container "space-y-8 max-w-md mx-auto"
   :tbody "bg-white [&>tr]:bg-white [&>tr]:border-b border-gray-200 [&>tr:hover]:bg-gray-50"})

(defn get-class [k]
  (get classes k))

(defn generate-style-map
  "Generates a map of styles dynamically based on the provided keys."
  [keys]
  (into {}
        (map (fn [k] [k (get-class k)]) keys)))

;; Replaced redundant `xxxx-views` functions with a single `views` function
(defn views
  "Returns a predefined style map for views."
  []
  (generate-style-map [:table :thead :th :th-actions :td-primary :td-secondary :td-actions
                       :link-primary :button-show :button-edit :button-delete :button-new
                       :form-label :form-input :cancel-button :container]))
