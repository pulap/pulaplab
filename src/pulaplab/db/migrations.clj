(ns pulaplab.db.migrations
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [next.jdbc :as jdbc]
            [pulaplab.db.core :as db])
  (:import (java.time ZonedDateTime ZoneId)
           (java.time.format DateTimeFormatter)
           (java.util UUID)))

(def migrations-dir "resources/migrations")

(defn ensure-migrations-table []
  (jdbc/execute! db/datasource
                 ["CREATE TABLE IF NOT EXISTS migrations (
       id TEXT PRIMARY KEY,
       datetime TEXT NOT NULL,
       name TEXT NOT NULL,
       created_at TIMESTAMP NOT NULL
     )"]))

(defn load-applied-migrations []
  (->> (jdbc/execute! db/datasource ["SELECT datetime, name FROM migrations"])
       (map #(str (:migrations/datetime %) "-" (:migrations/name %)))
       (set)))

(defn list-migration-files []
  (->> (file-seq (io/file migrations-dir))
       (filter #(.isFile %))
       (map #(.getName %))
       (sort)))

(defn parse-migration-file [filename]
  (let [[datetime name-with-ext] (str/split filename #"-" 2)
        name (str/replace name-with-ext #"\.sql$" "")
        full-path (str migrations-dir "/" filename)
        content (slurp full-path)
        parts (str/split content #"-- \+migrate ")]
    (let [up-part (some #(when (str/starts-with? (str/triml %) "Up") %) parts)]
      (if (nil? up-part)
        (throw (ex-info (str "No Up section found in migration file: " filename) {}))
        (let [clean-sql (-> up-part
                            (str/replace-first #"(?i)^Up\s*" "")
                            (str/trim))]
          {:datetime datetime
           :name name
           :up-sql clean-sql})))))

(defn now-iso8601 []
  (.format (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ssXXX")
           (ZonedDateTime/now (ZoneId/systemDefault))))

(defn apply-migration! [{:keys [datetime name up-sql]}]
  (println "Applying migration:" datetime "-" name)
  ;; (println "SQL to execute:")
  ;; (println up-sql)
  (jdbc/execute! db/datasource [up-sql])
  (jdbc/execute! db/datasource
                 ["INSERT INTO migrations (id, datetime, name, created_at) VALUES (?, ?, ?, ?)"
                  (str (UUID/randomUUID)) datetime name (now-iso8601)]))

(defn migrate! []
  (ensure-migrations-table)
  (let [applied (load-applied-migrations)
        files (list-migration-files)
        pending (remove applied files)]
    (doseq [filename pending]
      (let [migration (parse-migration-file filename)]
        (apply-migration! migration)))))
