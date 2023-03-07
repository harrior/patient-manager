(ns patients.db
  "Connects to the patients database and performs migrations."
  (:require [clojure.tools.logging :as log]
            [migratus.core :as migratus]
            [next.jdbc :as jdbc]
            [patients.config :as config]
            [patients.db-jsonb]))

(log/info "Connect to Database")

(def db-conn
  "The database connection"
  (jdbc/get-datasource config/db-spec))

(log/info "Connected to Database.")

;; Initializes the Migratus library for performing database migrations.
(migratus/init config/migratus-config)
;; Runs database migrations using the Migratus library.
(migratus/migrate config/migratus-config)
