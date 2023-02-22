(ns patients.db
  "Namespace for connecting to the patients database and performing migrations."
  (:require [migratus.core :as migratus]
            [next.jdbc :as jdbc]
            [patients.db-jsonb]
            [patients.config :as config]))

;; NOTE: needs use logger
(println "Connect to Database")
(def db-conn (jdbc/get-datasource config/db-spec))
(println "Connected.")

(println "Make migrations")
(migratus/init config/migratus-config)
(migratus/migrate config/migratus-config)
#_(jdbc/execute! db-conn ["CREATE TABLE IF NOT EXISTS patients (id UUID NOT NULL PRIMARY KEY, patient JSONB NOT NULL DEFAULT '{}')"])
(println "End migrations")
