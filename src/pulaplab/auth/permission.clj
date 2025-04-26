(ns pulaplab.auth.permission
  (:require [pulaplab.model.core :as base]))

(defn new-permission
  [{:keys [slug name description]
    :or {slug nil}}]
  (merge (base/base-attrs)
         {:slug slug
          :name name
          :description description}))
