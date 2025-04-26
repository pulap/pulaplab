(ns auth.role
  (:require [pulaplab.model.core :as base]))

(defn new-role
  [{:keys [slug name description status]
    :or {slug nil
         status nil}}]
  (merge (base/base-attrs)
         {:slug slug
          :name name
          :description description
          :status status}))
