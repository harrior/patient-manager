(ns patients.db
  "Connects to the patients database and performs migrations."
  (:require [migratus.core :as migratus]
            [next.jdbc :as jdbc]
            [patients.config :as config]
            [patients.db-jsonb]))

(defn db-conn
  "The database connection"
  []
  (jdbc/get-datasource config/db-spec))

(defn make-migrations
  "Initializes  Migratus library and runs database migrations"
  [migratus-config]
  (migratus/init migratus-config)
  (migratus/migrate migratus-config))
