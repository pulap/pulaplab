(ns auth.resource
  (:require [pulaplab.model.core :as base]))

(defn new-resource
  [{:keys [slug name description label type uri]
    :or {slug nil
         label nil
         type nil
         uri nil}}]
  (merge (base/base-attrs)
         {:slug slug
          :name name
          :description description
          :label label
          :type type
          :uri uri}))
