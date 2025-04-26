(ns pulaplab.model.core
  (:import (java.util UUID)))

(defn now []
  (java.time.Instant/now))

(defn generate-uuid []
  (str (UUID/randomUUID)))

(defn base-attrs
  []
  {:id (generate-uuid)
   :created_at (now)
   :updated_at (now)
   :created_by nil
   :updated_by nil})
